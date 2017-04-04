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
                [$class: 'UsernamePasswordMultiBinding', credentialsId: "${buildContext.getGroup()}-cloudfoundry", usernameVariable: 'CLOUD_FOUNDRY_USERNAME', passwordVariable: 'CLOUD_FOUNDRY_PASSWORD']]) {
            buildContext.changeStage('Push Container');
            buildContext.getScriptEngine().sh "cf login -u ${buildContext.getScriptEngine().env.CLOUD_FOUNDRY_USERNAME} -p ${buildContext.getScriptEngine().env.CLOUD_FOUNDRY_PASSWORD} -a \$BLUEMIX_API_URL"
            buildContext.getScriptEngine().sh "cf ic login"
            buildContext.getScriptEngine().sh "docker push \$DOCKER_REGISTRY_URL/${project}:${version}";
            buildContext.getScriptEngine().sh "docker push \$DOCKER_REGISTRY_URL/${project}:latest";

            // delete the images from jenkins
            buildContext.getScriptEngine().sh "docker rmi \$DOCKER_REGISTRY_URL/${project}:${version}";
            buildContext.getScriptEngine().sh "docker rmi \$DOCKER_REGISTRY_URL/${project}:latest";
        }
    }
}