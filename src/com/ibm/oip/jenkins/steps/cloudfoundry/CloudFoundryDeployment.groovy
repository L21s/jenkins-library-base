package com.ibm.oip.jenkins.ega.steps.deployment

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step

class CloudFoundryDeployment implements Step {
    private String targetEnvironment;

    public CloudFoundryDeployment(String targetEnvironment) {
        this.targetEnvironment = targetEnvironment;
    }

    public void doStep(BuildContext buildContext) {
        def version = buildContext.version.substring(2)
        buildContext.changeStage("Deploy to CF")
        buildContext.getScriptEngine().withCredentials([
                [$class: 'UsernamePasswordMultiBinding', credentialsId: "${buildContext.getGroup()}-nexus", usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD'],
                [$class: 'UsernamePasswordMultiBinding', credentialsId: "${buildContext.getGroup()}-cloudfoundry", usernameVariable: 'CLOUD_FOUNDRY_USERNAME', passwordVariable: 'CLOUD_FOUNDRY_PASSWORD']]) {
            // fetch distZip from Nexus
            buildContext.getScriptEngine().sh "wget --user ${buildContext.getScriptEngine().env.NEXUS_USERNAME} --password ${buildContext.getScriptEngine().env.NEXUS_PASSWORD} https://nexus.open-insurance-platform.com/repository/tk-releases/com/ibm/ega/${buildContext.project}/${version}/${buildContext.project}-${version}.zip -O deployment.zip"
            // fetch manifest.yml from Nexus
            buildContext.getScriptEngine().sh "wget --user ${buildContext.getScriptEngine().env.NEXUS_USERNAME} --password ${buildContext.getScriptEngine().env.NEXUS_PASSWORD} https://nexus.open-insurance-platform.com/repository/tk-releases/com/ibm/ega/${buildContext.project}/${version}/${buildContext.project}-${version}-cloudfoundry-${targetEnvironment}.yml -O deployment.yml"
            // use CF and override the file
            buildContext.getScriptEngine().sh "cf login -u ${buildContext.getScriptEngine().env.CLOUD_FOUNDRY_USERNAME} -p ${buildContext.getScriptEngine().env.CLOUD_FOUNDRY_PASSWORD} -a https://api.gcloud.eu-de.bluemix.net"
            buildContext.getScriptEngine().sh "cf push -f deployment.yml -p deployment.zip"
        }
    }
}