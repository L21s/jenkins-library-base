package com.ibm.oip.jenkins.steps.deployment.docker

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step

class IBMContainerDeployment implements Step {
    private String targetEnvironment;

    public IBMContainerDeployment(String targetEnvironment) {
        this.targetEnvironment = targetEnvironment;
    }

    public void doStep(BuildContext buildContext) {
        def version = buildContext.version.substring(2)
        buildContext.changeStage("Deploy to IBM Containers")
        buildContext.getScriptEngine().withCredentials([
                [$class: 'UsernamePasswordMultiBinding', credentialsId: "${buildContext.getGroup()}-nexus", usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD'],
                [$class: 'UsernamePasswordMultiBinding', credentialsId: "${buildContext.getGroup()}-cloudfoundry", usernameVariable: 'CLOUD_FOUNDRY_USERNAME', passwordVariable: 'CLOUD_FOUNDRY_PASSWORD']]) {
            buildContext.getScriptEngine().sh "cf login -u ${buildContext.getScriptEngine().env.CLOUD_FOUNDRY_USERNAME} -p ${buildContext.getScriptEngine().env.CLOUD_FOUNDRY_PASSWORD} -a https://api.gcloud.eu-de.bluemix.net"
            buildContext.getScriptEngine().sh "cf ic login"

            def oldGroupId = buildContext.getScriptEngine().sh(script: "cf ic group list | awk '\$2 ~ /${buildContext.getProject()}/ {print \$1}'", returnStdout: true);
            buildContext.getScriptEngine().sh "cf ic group create --name ${buildContext.getProject()}-${buildContext.getCommitId()} -p 8080 --min 2 --auto \$DOCKER_REGISTRY_URL/${buildContext.getProject()}:${buildContext.getVersion()}";
            buildContext.getScriptEngine().sh "cf ic route map -n ${buildContext.getProject()} -d gcloud.eu-de.mybluemix.net ${buildContext.getProject()}-${buildContext.getCommitId()}";
            buildContext.getScriptEngine().sh "cf ic group rm ${oldGroupId}"

        }
    }
}