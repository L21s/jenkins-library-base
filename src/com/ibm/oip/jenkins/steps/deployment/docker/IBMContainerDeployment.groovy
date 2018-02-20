package com.ibm.oip.jenkins.steps.deployment.docker

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step

class IBMContainerDeployment extends Step {
    private String targetEnvironment

    IBMContainerDeployment(String targetEnvironment) {
        this.targetEnvironment = targetEnvironment
    }

    void doStep(BuildContext buildContext) {
        buildContext.changeStage("Deploy to IBM Containers") {
            def secrets = [
                    [$class: 'VaultSecret', path: "secret/${buildContext.getGroup()}/environments/dev/deployment/bluemix", secretValues: [
                            [$class: 'VaultSecretValue', envVar: 'CLOUD_FOUNDRY_USERNAME', vaultKey: 'username'],
                            [$class: 'VaultSecretValue', envVar: 'CLOUD_FOUNDRY_PASSWORD', vaultKey: 'password']]]
            ]
            wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
                sh "cf login -o GHealthDev -s Dev -u ${env.CLOUD_FOUNDRY_USERNAME} -p ${env.CLOUD_FOUNDRY_PASSWORD} -a https://api.gcloud.eu-de.bluemix.net"
                buildContext.getScriptEngine().sh "cf ic login"

                def oldGroupId = sh(script: "cf ic group list | awk '\$2 ~ /${buildContext.getProject()}/ {print \$1}'", returnStdout: true);
                buildContext.getScriptEngine().sh "cf ic group create --name ${buildContext.getProject()}-${buildContext.getVersion()} -p 8080 --min 2 --auto \$DOCKER_REGISTRY_URL/\$DOCKER_REGISTRY_NAMESPACE/${buildContext.getProject()}:${buildContext.getVersion()}";
                sh "cf ic route map -n ${buildContext.getProject()} -d gcloud.eu-de.mybluemix.net ${buildContext.getProject()}-${buildContext.getVersion()}";
                sh "sleep 120"

                if (oldGroupId) {
                    sh "cf ic group rm ${oldGroupId}"
                }
            }
        }
    }
}