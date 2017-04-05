package com.ibm.oip.jenkins.steps.docker

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step

class CreatePushContainer implements Step {

    public void doStep(BuildContext buildContext) {
        buildContext.changeStage('Build Container');
        def project = buildContext.getProject();
        def version = buildContext.getVersion();
        buildContext.getScriptEngine().sh "docker build -t \$DOCKER_REGISTRY_URL/${project}:${version} -t \$DOCKER_REGISTRY_URL/${project}:latest .";

        buildContext.getScriptEngine().withCredentials([
                [$class: 'UsernamePasswordMultiBinding', credentialsId: "${buildContext.getGroup()}-docker-registry", usernameVariable: 'DOCKER_REGISTRY_USERNAME', passwordVariable: 'DOCKER_REGISTRY_PASSWORD']]) {

            buildContext.getScriptEngine().sh "docker login -u \$DOCKER_REGISTRY_USERNAME -p \$DOCKER_REGISTRY_PASSWORD \$DOCKER_REGISTRY_URL"
        }
        buildContext.getScriptEngine().sh "docker push \$DOCKER_REGISTRY_URL/${project}:${version}";
        // delete the images from jenkins
        buildContext.getScriptEngine().sh "docker rmi \$DOCKER_REGISTRY_URL/${project}:${version}";
    }
}