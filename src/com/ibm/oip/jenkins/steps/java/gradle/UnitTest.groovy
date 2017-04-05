package com.ibm.oip.jenkins.steps.java.gradle;

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step;

class UnitTest extends AbstractGradleStep {
    private BuildContext buildContext;

    void doStep(BuildContext buildContext) {
        this.buildContext = buildContext;
        buildContext.changeStage('Unit test');
        try {
            doGradleStep(buildContext, "test")
        } finally {
            publishTestResults();
        }
    }

    private void publishTestResults() {
        def reportDir = 'build/reports/tests/test';
        if (!buildContext.getScriptEngine().fileExists("${reportDir}") ){
            println "${reportDir} does not exist, not publishing anything."
            return
        }
        buildContext.getScriptEngine().publishHTML(target : [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "${reportDir}", reportFiles: 'index.html', reportName: 'Unit-Test Report'])
        buildContext.getScriptEngine().step([$class: 'JUnitResultArchiver', testResults: "**/build/test-results/test/*.xml"])
    }

}
