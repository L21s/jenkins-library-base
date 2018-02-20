package com.ibm.oip.jenkins.steps.java.gradle

import com.ibm.oip.jenkins.BuildContext

class PublishArtifacts extends AbstractGradleStep {
    @Override
    void doStep(BuildContext buildContext) {
        buildContext.changeStage("Publish artifacts") {
            doGradleStep(buildContext, "publish")
        }
    }
}

