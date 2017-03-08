package com.ibm.oip.jenkins.steps.docker

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step;

class AbstractCreateContainer implements Step {
    BuildContext buildContext;
    public void doStep(BuildContext buildContext) {
        this.buildContext = buildContext;
        buildContext.changeStage('Build Container');
        def project = buildContext.getProject();
        def version = buildContext.getVersion();
        buildContext.getScriptEngine().sh "docker build -t ${getDockerRegistryUri()}/${project}:${version} -t ${getDockerRegistryUri()}/${project}:latest .";
        buildContext.changeStage('Push Container');
        buildContext.getScriptEngine().sh "docker push ${getDockerRegistryUri()}/${project}:${version}";
        buildContext.getScriptEngine().sh "docker push ${getDockerRegistryUri()}/${project}:latest";
        buildContext.getScriptEngine().sh "docker rmi ${getDockerRegistryUri()}/${project}:${version}";
        buildContext.getScriptEngine().sh "docker rmi ${getDockerRegistryUri()}/${project}:latest";
    }

    public String getDockerRegistryUri() {
        throw new UnsupportedOperationException("Please provide docker registry url - override this method.");
    }
}