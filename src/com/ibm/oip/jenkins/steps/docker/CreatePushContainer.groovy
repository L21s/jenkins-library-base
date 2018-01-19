package com.ibm.oip.jenkins.steps.docker

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step

class CreatePushContainer implements Step {
    public void doStep(BuildContext buildContext) {
        buildContext.changeStage('Build Container');
        def project = buildContext.getProject();
        def version = buildContext.getVersion();
        buildContext.getScriptEngine().sh "docker build -t \$DOCKER_REGISTRY_URL/\$DOCKER_REGISTRY_NAMESPACE/${project}:${version} -t \$DOCKER_REGISTRY_URL/\$DOCKER_REGISTRY_NAMESPACE/${project}:latest .";

        def secrets = [
                [$class: 'VaultSecret', path: "secret/${buildContext.getGroup()}/tools/docker-registry", secretValues: [
                        [$class: 'VaultSecretValue', envVar: 'USERNAME', vaultKey: 'username'],
                        [$class: 'VaultSecretValue', envVar: 'PASSWORD', vaultKey: 'password']]]
        ]
        buildContext.getScriptEngine().wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
            buildContext.getScriptEngine().sh "docker login -u \$USERNAME -p \$PASSWORD \$DOCKER_REGISTRY_URL"
        }
        buildContext.getScriptEngine().sh "docker push \$DOCKER_REGISTRY_URL/\$DOCKER_REGISTRY_NAMESPACE/${project}:${version}";
        buildContext.getScriptEngine().sh "docker push \$DOCKER_REGISTRY_URL/\$DOCKER_REGISTRY_NAMESPACE/${project}:latest";

        // delete the images from jenkins
        buildContext.getScriptEngine().sh "docker rmi \$DOCKER_REGISTRY_URL/\$DOCKER_REGISTRY_NAMESPACE/${project}:${version}";
        buildContext.getScriptEngine().sh "docker rmi \$DOCKER_REGISTRY_URL/\$DOCKER_REGISTRY_NAMESPACE/${project}:latest";
    }
}
