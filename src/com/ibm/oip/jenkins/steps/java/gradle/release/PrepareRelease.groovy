package com.ibm.oip.jenkins.steps.java.gradle.release

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step

class PrepareRelease implements Step {
    private BuildContext buildContext;

    void doStep(BuildContext buildContext) {
        this.buildContext = buildContext;
        prepareRelease(getBumpFinderStrategy().retrieveBump());

        def nextVersion = retrieveNextVersion();
        buildContext.getScriptEngine().currentBuild.displayName = nextVersion;
        buildContext.setVersion(nextVersion);
    }

    public BumpFinderStrategy getBumpFinderStrategy() {
        return new DefaultBumpFinderStrategy();
    }

    private void prepareRelease(versionBump) {
        buildContext.changeStage('Create release');
        buildContext.getScriptEngine().sh("./gradlew createRelease -Prelease.disableRemoteCheck -Prelease.disableUncommittedCheck -Prelease.versionIncrementer=increment${versionBump}");
    }

    private String retrieveNextVersion() {
        return buildContext.getScriptEngine().sh(returnStdout: true, script: "git describe --tags --match 'v-[0-9\\.]*'").trim();
    }

}