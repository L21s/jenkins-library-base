package com.ibm.oip.jenkins.steps.docker

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step

class CreatePushContainer extends Step {
    void doStep(BuildContext buildContext) {
        buildContext.changeStage('Build Container') {
            def project = buildContext.getProject()
            def version = buildContext.getVersion()

            def imageName = buildContext.scriptEngine.env.DOCKER_REGISTRY_URL
            if(buildContext.scriptEngine.env.DOCKER_REGISTRY_NAMESPACE != null && buildContext.scriptEngine.env.DOCKER_REGISTRY_NAMESPACE != "") {
                imageName += "/${buildContext.scriptEngine.env.DOCKER_REGISTRY_NAMESPACE}"
            }
            imageName += "/${project}"

            sh "docker build -t ${imageName}:${version} -t ${imageName}:latest .";

            def secrets = [
                    [$class: 'VaultSecret', path: "secret/${buildContext.getGroup()}/tools/docker-registry", secretValues: [
                            [$class: 'VaultSecretValue', envVar: 'USERNAME', vaultKey: 'username'],
                            [$class: 'VaultSecretValue', envVar: 'PASSWORD', vaultKey: 'password']]]
            ]
            wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
                sh "docker login -u \$USERNAME -p \$PASSWORD \$DOCKER_REGISTRY_URL"
            }
            sh "docker push ${imageName}:${version}"
            sh "docker push ${imageName}:latest"

            // delete the images from jenkins
            sh "docker rmi ${imageName}:${version}"
            sh "docker rmi ${imageName}:latest"
        }
    }
}
