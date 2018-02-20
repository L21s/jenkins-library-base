package com.ibm.oip.jenkins.steps.deployment.cloudfoundry

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step

class CloudFoundryDeployment extends Step {
    private String targetEnvironment;

    CloudFoundryDeployment(String targetEnvironment) {
        this.targetEnvironment = targetEnvironment;
    }

    void doStep(BuildContext buildContext) {
        def version = buildContext.version.substring(2)
        buildContext.changeStage("Deploy to CF")
        def secrets = [
                [$class: 'VaultSecret', path: "secret/${buildContext.getGroup()}/environments/dev/deployment/bluemix", secretValues: [
                        [$class: 'VaultSecretValue', envVar: 'CLOUD_FOUNDRY_USERNAME', vaultKey: 'username'],
                        [$class: 'VaultSecretValue', envVar: 'CLOUD_FOUNDRY_PASSWORD', vaultKey: 'password']]],
                [$class: 'VaultSecret', path: "secret/${buildContext.getGroup()}/environments/tools/nexus", secretValues: [
                        [$class: 'VaultSecretValue', envVar: 'NEXUS_USERNAME', vaultKey: 'username'],
                        [$class: 'VaultSecretValue', envVar: 'NEXUS_PASSWORD', vaultKey: 'password']]]
        ]
        buildContext.getScriptEngine().wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
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