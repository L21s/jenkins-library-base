package com.ibm.oip.jenkins.steps.java.gradle

import com.ibm.oip.jenkins.BuildContext

class IntegrationTest extends AbstractGradleStep {

    void doStep(BuildContext buildContext) {
        buildContext.changeStage('Integration test') {
            def reportDir = 'build/reports/tests/integrationTest'
            try {
                doGradleStep(buildContext, "-x test integrationTest")
            } finally {
                publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "${reportDir}", reportFiles: 'index.html', reportName: 'Integrations-Test Report'])
                step([$class: 'JUnitResultArchiver', testResults: "**/build/test-results/integrationTest/*.xml"])
            }
        }
    }
}