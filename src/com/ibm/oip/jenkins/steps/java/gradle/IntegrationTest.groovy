package com.ibm.oip.jenkins.steps.java.gradle;

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step;

class IntegrationTest extends AbstractGradleStep {

    void doStep(BuildContext buildContext) {
        buildContext.changeStage('Integration test');
        def reportDir = 'build/reports/integrationTest';
        try {
            doGradleStep(buildContext, "-x test integrationTest")
        } finally {
            buildContext.getScriptEngine().publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "${reportDir}", reportFiles: 'index.html', reportName: 'Integrations-Test Report'])
            buildContext.getScriptEngine().step([$class: 'JUnitResultArchiver', testResults: "**/build/test-results/integrationTest/*.xml"])

        }

    }
}