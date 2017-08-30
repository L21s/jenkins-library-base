package com.ibm.oip.jenkins.steps.java.gradle;

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step

abstract class AbstractGradleStep implements Step {

    void doGradleStep(BuildContext buildContext, String gradleCommand) {
        doGradleStep(buildContext, gradleCommand, "");
    }

    GradleCommandResult doGradleStepReturnOutput(BuildContext buildContext, String gradleCommand) {
        def secrets = [
                [$class: 'VaultSecret', path: "secret/${buildContext.getGroup()}/tools/nexus", secretValues: [
                        [$class: 'VaultSecretValue', envVar: 'USERNAME', vaultKey: 'username'],
                        [$class: 'VaultSecretValue', envVar: 'PASSWORD', vaultKey: 'password']]]
        ]
        def statusCode
        buildContext.getScriptEngine().wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
            statusCode = buildContext.getScriptEngine().sh(script: "./gradlew ${gradleCommand} --no-daemon -PrepositoryUsername=${buildContext.getScriptEngine().env.USERNAME} -PrepositoryPassword=${buildContext.getScriptEngine().env.PASSWORD} -PnexusUsername=${buildContext.getScriptEngine().env.USERNAME} -PnexusPassword=${buildContext.getScriptEngine().env.PASSWORD} > gradle-command-output 2>&1", returnStatus:true);
        }
        def result = new GradleCommandResult();
        result.setStatusCode(statusCode);
        result.setOutput(buildContext.getScriptEngine().readFile("gradle-command-output").trim());
        return result;
    }

    void doGradleStep(BuildContext buildContext, String gradleCommand, String switches) {
        def secrets = [
                [$class: 'VaultSecret', path: "secret/${buildContext.getGroup()}/tools/nexus", secretValues: [
                        [$class: 'VaultSecretValue', envVar: 'USERNAME', vaultKey: 'username'],
                        [$class: 'VaultSecretValue', envVar: 'PASSWORD', vaultKey: 'password']]]
        ]
        buildContext.getScriptEngine().wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
            buildContext.getScriptEngine().sh("./gradlew ${gradleCommand} --no-daemon -PrepositoryUsername=${buildContext.getScriptEngine().env.USERNAME} -PrepositoryPassword=${buildContext.getScriptEngine().env.PASSWORD} -PnexusUsername=${buildContext.getScriptEngine().env.USERNAME} -PnexusPassword=${buildContext.getScriptEngine().env.PASSWORD} ${switches}");
        }
    }
}
