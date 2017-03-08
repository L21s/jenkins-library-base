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

        buildContext.getScriptEngine().withCredentials([string(credentialsId: 'SONARQUBE_USER', variable: 'SONARQUBE_USER'),
                                                        string(credentialsId: 'SONARQUBE_PASSWORD', variable: 'SONARQUBE_PASSWORD'),
                                                        string(credentialsId: 'GITHUB_OAUTH_TOKEN', variable: 'GITHUB_OAUTH_TOKEN')]) {
            buildContext.getScriptEngine().sh("./gradlew sonarqube -Dsonar.branch=${buildContext.branch} " +
                    "-Dsonar.buildbreaker.skip=$skip " +
                    "-Dsonar.host.url='${getSonarqubeUri()} " +
                    "-Dsonar.login='${buildContext.getScriptEngine().env.SONARQUBE_USER}' " +
                    "-Dsonar.password='${buildContext.getScriptEngine().env.SONARQUBE_PASSWORD}'");
        }
    }
}