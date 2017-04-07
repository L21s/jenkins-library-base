package com.ibm.oip.jenkins.steps.java.gradle;

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step

abstract class AbstractGradleStep implements Step {

    void doGradleStep(BuildContext buildContext, String gradleCommand) {
        doGradleStep(buildContext, gradleCommand, "");
    }

    String doGradleStepReturnOutput(BuildContext buildContext, String gradleCommand) {
        def output;
        def secrets = [
                [$class: 'VaultSecret', path: "secret/${buildContext.getGroup()}/tools/nexus", secretValues: [
                        [$class: 'VaultSecretValue', envVar: 'USERNAME', vaultKey: 'username'],
                        [$class: 'VaultSecretValue', envVar: 'PASSWORD', vaultKey: 'password']]]
        ]
        buildContext.getScriptEngine().wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
            output = buildContext.getScriptEngine().sh( script: "./gradlew ${gradleCommand} -PrepositoryUsername=${buildContext.getScriptEngine().env.USERNAME} -PrepositoryPassword=${buildContext.getScriptEngine().env.PASSWORD} -PnexusUsername=${buildContext.getScriptEngine().env.USERNAME} -PnexusPassword=${buildContext.getScriptEngine().env.PASSWORD}",
                                                        returnStdOut: true);
        }

        return output;
    }

    void doGradleStep(BuildContext buildContext, String gradleCommand, String switches) {
        def secrets = [
                [$class: 'VaultSecret', path: "secret/${buildContext.getGroup()}/tools/nexus", secretValues: [
                        [$class: 'VaultSecretValue', envVar: 'USERNAME', vaultKey: 'username'],
                        [$class: 'VaultSecretValue', envVar: 'PASSWORD', vaultKey: 'password']]]
        ]
        buildContext.getScriptEngine().wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
            buildContext.getScriptEngine().sh("./gradlew ${gradleCommand} -PrepositoryUsername=${buildContext.getScriptEngine().env.USERNAME} -PrepositoryPassword=${buildContext.getScriptEngine().env.PASSWORD} -PnexusUsername=${buildContext.getScriptEngine().env.USERNAME} -PnexusPassword=${buildContext.getScriptEngine().env.PASSWORD} ${switches}");
        }
    }
}
