package com.ibm.oip.jenkins.ega.steps.deployment

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step

class IBMContainerDeployment implements Step {
    private String targetEnvironment;

    public CloudFoundryDeployment(String targetEnvironment) {
        this.targetEnvironment = targetEnvironment;
    }

    public void doStep(BuildContext buildContext) {
        def version = buildContext.version.substring(2)
        buildContext.changeStage("Deploy to IBM Containers")
        buildContext.getScriptEngine().withCredentials([
                [$class: 'UsernamePasswordMultiBinding', credentialsId: "${buildContext.getGroup()}-nexus", usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD'],
                [$class: 'UsernamePasswordMultiBinding', credentialsId: "${buildContext.getGroup()}-cloudfoundry", usernameVariable: 'CLOUD_FOUNDRY_USERNAME', passwordVariable: 'CLOUD_FOUNDRY_PASSWORD']]) {
            buildContext.getScriptEngine().sh "cf login -u ${buildContext.getScriptEngine().env.CLOUD_FOUNDRY_USERNAME} -p ${buildContext.getScriptEngine().env.CLOUD_FOUNDRY_PASSWORD} -a https://api.gcloud.eu-de.bluemix.net"
            buildContext.getScriptEngine().sh "cf ic login"

            // do sth to acutally deploy
        }
    }
}