package com.ibm.oip.jenkins.steps.java.gradle.release

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.VersionBumpDeterminer
import com.ibm.oip.jenkins.steps.github.GithubPRLabelVersionBumpDeterminer
import com.ibm.oip.jenkins.steps.java.gradle.AbstractGradleStep

class PrepareRelease extends AbstractGradleStep {
    private BuildContext buildContext;
    private VersionBumpDeterminer versionBumpDeterminer;

    PrepareRelease(def defaultBump) {
        this.versionBumpDeterminer = new GithubPRLabelVersionBumpDeterminer(defaultBump);
    }

    PrepareRelease() {
        this.versionBumpDeterminer = new GithubPRLabelVersionBumpDeterminer();
    }
    def bumpMapping = [
        "patch": "incrementPatch",
        "minor": "incrementMinor",
        "major": "incrementMajor"
    ]

    void doStep(BuildContext buildContext) {
        this.buildContext = buildContext;
        prepareRelease(buildContext, bumpMapping[versionBumpDeterminer.determineVersionDump(buildContext)].trim());

        def nextVersion = retrieveNextVersion();
        buildContext.getScriptEngine().currentBuild.displayName = nextVersion;
        buildContext.setVersion(nextVersion);
    }

    private void prepareRelease(buildContext, versionBump) {
        buildContext.changeStage('Create release');
        doGradleStep(buildContext, "createRelease -Prelease.disableRemoteCheck -Prelease.disableUncommittedCheck -Prelease.versionIncrementer=${versionBump} ");
    }

    private String retrieveNextVersion() {
        return buildContext.getScriptEngine().sh(returnStdout: true, script: "git describe --tags --match 'v-[0-9\\.]*'").trim();
    }

}