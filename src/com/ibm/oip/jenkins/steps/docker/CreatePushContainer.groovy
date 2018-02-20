package com.ibm.oip.jenkins.steps.docker

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step

class CreatePushContainer extends Step {
    void doStep(BuildContext buildContext) {
        buildContext.changeStage('Build Container') {
            def project = buildContext.getProject()
            def version = buildContext.getVersion()
            sh "docker build -t \$DOCKER_REGISTRY_URL/\$DOCKER_REGISTRY_NAMESPACE/${project}:${version} -t \$DOCKER_REGISTRY_URL/\$DOCKER_REGISTRY_NAMESPACE/${project}:latest .";

            def secrets = [
                    [$class: 'VaultSecret', path: "secret/${buildContext.getGroup()}/tools/docker-registry", secretValues: [
                            [$class: 'VaultSecretValue', envVar: 'USERNAME', vaultKey: 'username'],
                            [$class: 'VaultSecretValue', envVar: 'PASSWORD', vaultKey: 'password']]]
            ]
            wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
                sh "docker login -u \$USERNAME -p \$PASSWORD \$DOCKER_REGISTRY_URL"
            }
            sh "docker push \$DOCKER_REGISTRY_URL/\$DOCKER_REGISTRY_NAMESPACE/${project}:${version}"
            sh "docker push \$DOCKER_REGISTRY_URL/\$DOCKER_REGISTRY_NAMESPACE/${project}:latest"

            // delete the images from jenkins
            sh "docker rmi \$DOCKER_REGISTRY_URL/\$DOCKER_REGISTRY_NAMESPACE/${project}:${version}"
            sh "docker rmi \$DOCKER_REGISTRY_URL/\$DOCKER_REGISTRY_NAMESPACE/${project}:latest"
        }
    }
}
