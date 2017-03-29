package com.ibm.oip.jenkins.steps.docker

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step;

class CreatePushContainer implements Step {
    public void doStep(BuildContext buildContext) {
        buildContext.changeStage('Build Container');
        def project = buildContext.getProject();
        def version = buildContext.getVersion();
        buildContext.getScriptEngine().sh "docker build -t \$DOCKER_REGISTRY_URL/${project}:${version} -t \$DOCKER_REGISTRY_URL/${project}:latest .";
        buildContext.changeStage('Push Container');
        buildContext.getScriptEngine().sh "docker push \$DOCKER_REGISTRY_URL/${project}:${version}";
        buildContext.getScriptEngine().sh "docker push \$DOCKER_REGISTRY_URL/${project}:latest";
        buildContext.getScriptEngine().sh "docker rmi \$DOCKER_REGISTRY_URL/${project}:${version}";
        buildContext.getScriptEngine().sh "docker rmi \$DOCKER_REGISTRY_URL/${project}:latest";

    }
}