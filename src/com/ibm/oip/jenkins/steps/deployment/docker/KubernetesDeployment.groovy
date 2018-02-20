package com.ibm.oip.jenkins.steps.deployment.docker

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step
import com.ibm.oip.jenkins.util.FileTemplater

class KubernetesDeployment extends Step {
    private String targetEnvironment;
    private BuildContext buildContext;

    KubernetesDeployment(String targetEnvironment) {
        this.targetEnvironment = targetEnvironment
    }


    @Override
    void doStep(BuildContext buildContext) {
        this.buildContext = buildContext
        buildContext.changeStage("Deploy to Kubernetes") {
            configFileProvider(
                    [configFile(fileId: "kubernetes-${targetEnvironment}", variable: 'KUBERNETES_CONFIG'),
                     configFile(fileId: "kubernetes-${targetEnvironment}-pem", variable: 'KUBERNETES_CA')]) {
                def variables = load env.KUBERNETES_CONFIG
                withEnv(variables) {
                    def secrets = [
                            [$class: 'VaultSecret', path: "${env.TOKEN_VAULT_PATH}", secretValues: [
                                    [$class: 'VaultSecretValue', envVar: 'KUBERNETES_TOKEN', vaultKey: 'token']]]
                    ]
                    wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
                        templateApplicationYml();
                        kubectl("apply -f kubernetes/")
                    }
                }
            }
        }
    }

    void templateApplicationYml() {
        FileTemplater templater = new FileTemplater(buildContext, "kubernetes/application.yml");
        templater.template("%VERSION%", buildContext.getVersion().trim())
        templater.template("%NAMESPACE%", env.NAMESPACE)
        templater.template("%INGRESS_BASE_URL%", env.INGRESS_BASE_URL)
    }

    void kubectl(String cmd) {
        sh("kubectl " +
                "--namespace ${env.NAMESPACE} " +
                "--certificate-authority ${env.KUBERNETES_CA} " +
                "--server ${env.MASTER_URL} " +
                "--token ${env.KUBERNETES_TOKEN} " +
                "${cmd}")

    }
}
