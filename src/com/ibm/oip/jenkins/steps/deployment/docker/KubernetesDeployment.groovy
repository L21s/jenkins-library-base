package com.ibm.oip.jenkins.steps.deployment.docker

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step
import com.ibm.oip.jenkins.util.FileTemplater

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
                [buildContext.getScriptEngine().configFile(fileId: "kubernetes-${targetEnvironment}", variable: 'KUBERNETES_CONFIG'),
                 buildContext.getScriptEngine().configFile(fileId: "kubernetes-${targetEnvironment}-pem", variable: 'KUBERNETES_CA')]) {
            def variables = buildContext.getScriptEngine().load buildContext.getScriptEngine().env.KUBERNETES_CONFIG
            buildContext.getScriptEngine().withEnv(variables) {
                def secrets = [
                        [$class: 'VaultSecret', path: "${buildContext.getScriptEngine().env.TOKEN_VAULT_PATH}", secretValues: [
                                [$class: 'VaultSecretValue', envVar: 'KUBERNETES_TOKEN', vaultKey: 'token']]]
                ]
                buildContext.getScriptEngine().wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
                    replaceVersionInAllKubernetesFiles();
                    buildContext.getScriptEngine().sh("cat kubernetes/application.yml");
                    kubectl("apply -f kubernetes/");
                }
            }
        }
    }

    void replaceVersionInAllKubernetesFiles() {
        FileTemplater templater = new FileTemplater(buildContext, "kubernetes/application.yml");
        templater.template("%VERSION%", buildContext.getVersion().trim());
    }

    void kubectl(String cmd) {
        buildContext.getScriptEngine().sh("kubectl " +
                "--namespace ${buildContext.getScriptEngine().env.NAMESPACE} " +
                "--certificate-authority ${buildContext.getScriptEngine().env.KUBERNETES_CA} " +
                "--server ${buildContext.getScriptEngine().env.MASTER_URL} " +
                "--token ${buildContext.getScriptEngine().env.KUBERNETES_TOKEN} " +
                "${cmd}");

    }
}