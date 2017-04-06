package com.ibm.oip.jenkins.steps.deployment.docker

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step

class KubernetesDeployment implements Step {
    private String targetEnvironment;

    public KubernetesDeployment(String targetEnvironment) {
        this.targetEnvironment = targetEnvironment;
    }


    @Override
    void doStep(BuildContext buildContext) {
        def secrets = [
                [$class: 'VaultSecret', path: "secret/${buildContext.getGroup()}/environments/dev/deployment/kubernetes", secretValues: [
                        [$class: 'VaultSecretValue', envVar: 'BX_CLI_APIKEY', vaultKey: 'api_key']]]
        ]
        buildContext.getScriptEngine().wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
            buildContext.getScriptEngine().sh "bx login --apikey \$BX_CLI_APIKEY"
            def exportStatement = buildContext.getScriptEngine().sh(script: "bx cs cluster-config \$BX_K8S_CLUSTER_NAME | tail -n1", returnStdout: true);

            buildContext.getScriptEngine().sh "export KUBECONFIG=${extractPath(exportStatement)}";
            buildContext.getScriptEngine().sh "kubectl set image deployment/${buildContext.getProject()} ${buildContext.getProject()}=\$DOCKER_REGISTRY_URL/${buildContext.getProject()}:${buildContext.getVersion()}";
            // clean up
            buildContext.geutScriptEngine().sh "export KUBECONFIG=";
            buildContext.getScriptEngine().sh "rm -rf ${extractPath(exportStatement)}";
        }
    }

    @NonCPS
    String extractPath(def exportStatement) {
        def configPath = exportStatement = ".*=(.*)";~
        configPath ? configPath[0][1] : null
    }
}