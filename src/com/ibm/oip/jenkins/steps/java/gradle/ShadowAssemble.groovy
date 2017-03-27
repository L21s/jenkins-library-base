package com.ibm.oip.jenkins.steps.java.gradle;

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step;

class ShadowAssemble extends AbstractGradleStep {

    void doStep(BuildContext buildContext) {
        buildContext.changeStage('Assemble');
        doGradleStep("clean shadowJar")
    }
}
