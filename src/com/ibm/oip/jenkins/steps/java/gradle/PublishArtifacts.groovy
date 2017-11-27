package com.ibm.oip.jenkins.steps.java.gradle

import com.ibm.oip.jenkins.BuildContext

class PublishArtifacts extends AbstractGradleStep {
    @Override
    public void doStep(BuildContext buildContext) {
        buildContext.changeStage("Publish artifacts")
        doGradleStep(buildContext, "publishMavenPublicationToMavenRepository");
    }
}

