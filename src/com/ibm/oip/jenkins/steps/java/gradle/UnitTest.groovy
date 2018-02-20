package com.ibm.oip.jenkins.steps.java.gradle

import com.ibm.oip.jenkins.BuildContext

class UnitTest extends AbstractGradleStep {
    private BuildContext buildContext;

    void doStep(BuildContext buildContext) {
        this.buildContext = buildContext;
        buildContext.changeStage('Unit test') {
            try {
                doGradleStep(buildContext, "test")
            } finally {
                def reportDir = 'build/reports/tests/test';
                if (!fileExists("${reportDir}")) {
                    println "${reportDir} does not exist, not publishing anything."
                    return
                }
                publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "${reportDir}", reportFiles: 'index.html', reportName: 'Unit-Test Report'])
                step([$class: 'JUnitResultArchiver', testResults: "**/build/test-results/**/*.xml"])
            }
        }
    }
}