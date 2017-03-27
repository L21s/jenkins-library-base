package com.ibm.oip.jenkins.steps.java.gradle.release

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step

class PrepareRelease extends AbstractGradleStep {
    private BuildContext buildContext;

    void doStep(BuildContext buildContext) {
        this.buildContext = buildContext;
        prepareRelease(determineVersionDump());

        def nextVersion = retrieveNextVersion();
        buildContext.getScriptEngine().currentBuild.displayName = nextVersion;
        buildContext.setVersion(nextVersion);
    }

    public String determineVersionDump() {
        return "Patch";
    }

    private void prepareRelease(versionBump) {
        buildContext.changeStage('Create release');
        doGradleStep("createRelease -Prelease.disableRemoteCheck -Prelease.disableUncommittedCheck -Prelease.versionIncrementer=increment${versionBump} ");
    }

    private String retrieveNextVersion() {
        return buildContext.getScriptEngine().sh(returnStdout: true, script: "git describe --tags --match 'v-[0-9\\.]*'").trim();
    }

}