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
            doGradleStep(buildContext, "sonarqube -Dsonar.branch=${buildContext.branch} " +
                    "-Dsonar.buildbreaker.skip=$skip " +
                    "-Dsonar.host.url=\$SONARQUBE_URL " +
                    "-Dsonar.login='${buildContext.getScriptEngine().env.USERNAME}' " +
                    "-Dsonar.password='${buildContext.getScriptEngine().env.PASSWORD}' ");
        }
    }
}
