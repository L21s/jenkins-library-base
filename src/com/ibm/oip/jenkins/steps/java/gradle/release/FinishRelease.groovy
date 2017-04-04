package com.ibm.oip.jenkins.steps.java.gradle.release

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.java.gradle.AbstractGradleStep

class FinishRelease extends AbstractGradleStep {
    void doStep(BuildContext buildContext) {
        buildContext.changeStage('Publish release');
        buildContext.getScriptEngine().sh ("git push --tags");
        doGradleStep(buildContext, "publish --stracktrace");
    }
}
