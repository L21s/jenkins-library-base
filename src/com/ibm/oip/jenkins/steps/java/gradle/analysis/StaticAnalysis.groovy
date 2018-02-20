package com.ibm.oip.jenkins.steps.java.gradle.analysis

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.java.gradle.AbstractGradleStep

class StaticAnalysis extends AbstractGradleStep {
    def skip

    StaticAnalysis(def skip) {
        this.skip = skip
    }

    void doStep(BuildContext buildContext) {
        buildContext.changeStage('Static analysis') {
            def secrets = [
                    [$class: 'VaultSecret', path: "secret/${buildContext.getGroup()}/tools/sonarqube", secretValues: [
                            [$class: 'VaultSecretValue', envVar: 'SONARQUBE_TOKEN', vaultKey: 'api_token']]]
            ]
            wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
                doGradleStep(buildContext, "sonarqube " +
                        "-Dsonar.buildbreaker.skip=$skip " +
                        "-Dsonar.host.url=\$SONARQUBE_URL " +
                        "-Dsonar.login='${env.SONARQUBE_TOKEN}' " +
                        "-Dsonar.jacoco.itReportPath=build/jacoco/integrationTest.exec -Dsonar.jacoco.reportPath=build/jacoco/test.exec");
                // The last line should acutally not be needed since SQ 6.2 - but without it does not recognize the coverage
            }
        }
    }
}
