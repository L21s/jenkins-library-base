package com.ibm.oip.jenkins.steps.deployment.docker

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step

class IBMContainerDeployment implements Step {
    private String targetEnvironment;

    public IBMContainerDeployment(String targetEnvironment) {
        this.targetEnvironment = targetEnvironment;
    }

    public void doStep(BuildContext buildContext) {
        buildContext.changeStage("Deploy to IBM Containers")
        def secrets = [
                [$class: 'VaultSecret', path: "secret/${buildContext.getGroup()}/environments/dev/deployment/cloudfoundry", secretValues: [
                        [$class: 'VaultSecretValue', envVar: 'CLOUD_FOUNDRY_USERNAME', vaultKey: 'username'],
                        [$class: 'VaultSecretValue', envVar: 'CLOUD_FOUNDRY_PASSWORD', vaultKey: 'password']]]
        ]
        buildContext.getScriptEngine().wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
            buildContext.getScriptEngine().sh "cf login -u ${buildContext.getScriptEngine().env.CLOUD_FOUNDRY_USERNAME} -p ${buildContext.getScriptEngine().env.CLOUD_FOUNDRY_PASSWORD} -a https://api.gcloud.eu-de.bluemix.net"
            buildContext.getScriptEngine().sh "cf ic login"

            def oldGroupId = buildContext.getScriptEngine().sh(script: "cf ic group list | awk '\$2 ~ /${buildContext.getProject()}/ {print \$1}'", returnStdout: true);
            buildContext.getScriptEngine().sh "cf ic group create --name ${buildContext.getProject()}-${buildContext.getVersion()} -p 8080 --min 2 --auto \$DOCKER_REGISTRY_URL/${buildContext.getProject()}:${buildContext.getVersion()}";
            buildContext.getScriptEngine().sh "cf ic route map -n ${buildContext.getProject()} -d gcloud.eu-de.mybluemix.net ${buildContext.getProject()}-${buildContext.getVersion()}";
            buildContext.getScriptEngine().sh "sleep 120"
            buildContext.getScriptEngine().sh "cf ic group rm ${oldGroupId}"
        }
    }
}