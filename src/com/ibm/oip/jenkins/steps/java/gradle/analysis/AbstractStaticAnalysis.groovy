package com.ibm.oip.jenkins.steps.java.gradle.analysis

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step

class AbstractStaticAnalysis implements Step {
    def skip;

    public AbstractStaticAnalysis(def skip) {
        this.skip = skip;
    }

    String getSonarqubeUri() {
        throw new UnsupportedOperationException("Please override this method and provide an sonarqube URI like https://devstack.ibm-insurance-platform.com/sonarqube")
    }

    void doStep(BuildContext buildContext) {
        buildContext.changeStage('Static analysis');

        buildContext.getScriptEngine() withCredentials([
                            [$class: 'UsernamePasswordMultiBinding', credentialsId: 'sonarqube', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            buildContext.getScriptEngine().sh("./gradlew sonarqube -Dsonar.branch=${buildContext.branch} " +
                    "-Dsonar.buildbreaker.skip=$skip " +
                    "-Dsonar.host.url=${getSonarqubeUri()} " +
                    "-Dsonar.login='${buildContext.getScriptEngine().env.USERNAME}' " +
                    "-Dsonar.password='${buildContext.getScriptEngine().env.PASSWORD}'");
        }
    }
}
