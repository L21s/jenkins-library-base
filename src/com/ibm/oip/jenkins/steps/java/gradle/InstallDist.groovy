package com.ibm.oip.jenkins.steps.java.gradle

import com.ibm.oip.jenkins.BuildContext

class InstallDist extends AbstractGradleStep {
    void doStep(BuildContext buildContext) {
        buildContext.changeStage('Assemble (Distribution)');
        doGradleStep(buildContext, "clean installDist")
    }
}