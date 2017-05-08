package com.ibm.oip.jenkins.steps.deployment.docker

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step

class KubernetesDeployment implements Step {
    private String targetEnvironment;
    private BuildContext buildContext;

    public KubernetesDeployment(String targetEnvironment) {
        this.targetEnvironment = targetEnvironment;
    }


    @Override
    void doStep(BuildContext buildContext) {
        this.buildContext = buildContext;
        buildContext.getScriptEngine().configFileProvider(
                [buildContext.getScriptEngine().configFile(fileId: "kubernetes-${targetEnvironment}", variable: 'KUBERNETES_CONFIG')]) {
            def variables = buildContext.getScriptEngine().load buildContext.getScriptEngine().env.KUBERNETES_CONFIG
            buildContext.getScriptEngine().withEnv(variables) {
                def secrets = [
                        [$class: 'VaultSecret', path: "${buildContext.getScriptEngine().env.TOKEN_VAULT_PATH}", secretValues: [
                                [$class: 'VaultSecretValue', envVar: 'KUBERNETES_TOKEN', vaultKey: 'token']]]
                ]
                buildContext.getScriptEngine().wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
                    kubectl("apply -f kubernetes/");
                }
            }
        }
    }

    void kubectl(String cmd) {
        buildContext.getScriptEngine().sh("kubectl " +
                                        "--namespace ${buildContext.getScriptEngine().env.NAMESPACE} " +
                                        "--server ${buildContext.getScriptEngine().env.MASTER_URL} " +
                                        "--token ${buildContext.getScriptEngine().env.KUBERNETES_TOKEN} " +
                                        "${cmd}");
    }
}