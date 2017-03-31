package com.ibm.oip.jenkins.steps.java.gradle.release

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.java.gradle.AbstractGradleStep

class PrepareRelease extends AbstractGradleStep {
    private BuildContext buildContext;

    void doStep(BuildContext buildContext) {
        this.buildContext = buildContext;
        prepareRelease(buildContext, determineVersionDump());

        def nextVersion = retrieveNextVersion();
        buildContext.getScriptEngine().currentBuild.displayName = nextVersion;
        buildContext.setVersion(nextVersion);
    }

    @NonCPS
    String retrievePrId() {
        def pr = buildContext.getCommitMessage() =~ ".*Merge pull request #(\\d+).*"
        if(!pr.hasGroup()) {
            return null;
        }
        return pr[0][1];
    }

    public String determineVersionDump() {
        def prNumber = retrievePrId()
        if (!prNumber) {
            return "Patch"
        }

        buildContext.getScriptEngine() withCredentials([[$class: 'StringBinding', credentialsId: "${buildContext.getGroup()}-sonarqube-github-reporter", variable: 'GITHUB_OAUTH_TOKEN']]) {
            def output = buildContext.getScriptEngine().sh("curl -X GET -H 'Authorization: token ${buildContext.getScriptEngine().env.GITHUB_OAUTH_TOKEN}' \$GITHUB_API_URL/repos/${buildContext.getGroup()}/${buildContext.getProject()}/issues/${prNumber}/labels | jq -r '.[].name' > labels.txt")
            String[] labels = new File('labels.txt')
            def version = "patch";
            labels.any { label ->
                if (label == "major" || label == "minor") {
                    buildContext.getScriptEngine().sh "tag is major or minor"
                    version = label;
                    return true;
                }

                buildContext.getScriptEngine().sh "tag is NOT major or minor"
            }
        }

        return version;
    }

    private void prepareRelease(buildContext, versionBump) {
        buildContext.changeStage('Create release');
        doGradleStep(buildContext, "createRelease -Prelease.disableRemoteCheck -Prelease.disableUncommittedCheck -Prelease.versionIncrementer=increment${versionBump} ");
    }

    private String retrieveNextVersion() {
        return buildContext.getScriptEngine().sh(returnStdout: true, script: "git describe --tags --match 'v-[0-9\\.]*'").trim();
    }

}