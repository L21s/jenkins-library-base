package com.ibm.oip.jenkins.steps.java.gradle

import com.ibm.oip.jenkins.BuildContext

class Assemble extends AbstractGradleStep {

    void doStep(BuildContext buildContext) {
        buildContext.changeStage('Assemble') {
            doGradleStep(buildContext, "clean assemble")
        }
    }
}
