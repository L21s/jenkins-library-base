package com.ibm.oip.jenkins.steps.java.gradle

import com.ibm.oip.jenkins.BuildContext

class ShadowAssemble extends AbstractGradleStep {
    void doStep(BuildContext buildContext) {
        buildContext.changeStage('Assemble') {
            doGradleStep(buildContext, "clean shadowJar")
        }
    }
}
