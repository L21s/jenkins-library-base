package com.ibm.oip.jenkins.steps.java.gradle.release

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.java.gradle.AbstractGradleStep

class PrepareRelease extends AbstractGradleStep {
    private BuildContext buildContext;

    def bumpMapping = [
        "patch": "incrementPatch",
        "minor": "incrementMinor",
        "major": "incrementMajor"
    ]

    void doStep(BuildContext buildContext) {
        this.buildContext = buildContext;
        prepareRelease(buildContext, determineVersionDump());

        def nextVersion = retrieveNextVersion();
        buildContext.getScriptEngine().currentBuild.displayName = nextVersion;
        buildContext.setVersion(nextVersion);
    }

    public String determineVersionDump() {
        def prNumber = retrievePrId(buildContext.getCommitMessage())
        if (!prNumber) {
            return bumpMapping["patch"].trim()
        }

        def bump = "patch";
        def secrets = [
                [$class: 'VaultSecret', path: "secret/${buildContext.getGroup()}/tools/sonarqube", secretValues: [
                        [$class: 'VaultSecretValue', envVar: 'GITHUB_OAUTH_TOKEN', vaultKey: 'github_token']]]
        ]
        buildContext.getScriptEngine().wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
            def output = buildContext.getScriptEngine().sh("curl -X GET -H 'Authorization: token ${buildContext.getScriptEngine().env.GITHUB_OAUTH_TOKEN}' \$GITHUB_API_URL/repos/${buildContext.getGroup()}/${buildContext.getProject()}/issues/${prNumber}/labels | jq -r '.[].name' > labels.txt")
            def labels = buildContext.getScriptEngine().readFile('labels.txt').split("\\n")

            for(int i = 0; i < labels.size(); i++) {
                if(labels[i] == "major" || labels[i] == "minor") {
                    bump = labels[i];
                    break;
                }
            }
        }

        return bumpMapping[bump].trim();
    }


    def retrievePrId(commitMsg) {
        def pr = commitMsg =~ ".*Merge pull request #(\\d+).*"
        pr ? pr[0][1] : null
    }

    private void prepareRelease(buildContext, versionBump) {
        buildContext.changeStage('Create release');
        doGradleStep(buildContext, "createRelease -Prelease.disableRemoteCheck -Prelease.disableUncommittedCheck -Prelease.versionIncrementer=${versionBump} ");
    }

    private String retrieveNextVersion() {
        return buildContext.getScriptEngine().sh(returnStdout: true, script: "git describe --tags --match 'v-[0-9\\.]*'").trim();
    }

}