package com.ibm.oip.jenkins.steps.java.gradle.analysis

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.java.gradle.AbstractGradleStep

class StaticAnalysis extends AbstractGradleStep {
    def skip;

    public StaticAnalysis(def skip) {
        this.skip = skip;
    }

    void doStep(BuildContext buildContext) {
        buildContext.changeStage('Static analysis');

        buildContext.getScriptEngine().withCredentials([
                            [$class: 'UsernamePasswordMultiBinding', credentialsId: "${buildContext.getGroup()}-sonarqube", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            doGradleStep(buildContext, "sonarqube " +
                    "-Dsonar.buildbreaker.skip=$skip " +
                    "-Dsonar.host.url=\$SONARQUBE_URL " +
                    "-Dsonar.login='${buildContext.getScriptEngine().env.USERNAME}' " +
                    "-Dsonar.password='${buildContext.getScriptEngine().env.PASSWORD}' " +
                    "-Dsonar.jacoco.itReportPath=build/jacoco/integrationTest.exec -Dsonar.jacoco.reportPath=build/jacoco/test.exec");
            // The last line should acutally not be needed since SQ 6.2 - but without it does not recognize the coverage
        }
    }
}
