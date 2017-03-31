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
    public String determineVersionDump() {
        // get PR number from commit
        def pr = buildContext.getCommitMessage() =~ ".*Merge pull request #(\\d+).*"

        if (!pr.hasGroup()) {
            return "Patch"
        }
        def prNumber = pr[0][1];

        buildContext.getScriptEngine() withCredentials([[$class: 'StringBinding', credentialsId: "${buildContext.getGroup()}-sonarqube-github-reporter", variable: 'GITHUB_OAUTH_TOKEN_NEW']]) {
            def output = buildContext.getScriptEngine().sh(returnStdout: true, script: "curl -X GET -H 'Authorization: token cd96d479772924b4ab66d30e1235e43f1241496a' \$GITHUB_API_URL/repos/${buildContext.getGroup()}/${buildContext.getProject()}/issues/${prNumber}/labels")
            buildContext.getScriptEngine().sh "echo before loading labels"
            String[] labels = new File('labels.txt')
            buildContext.getScriptEngine().sh "echo after loading labels"
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