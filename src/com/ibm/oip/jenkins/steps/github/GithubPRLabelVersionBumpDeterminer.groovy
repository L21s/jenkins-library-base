package com.ibm.oip.jenkins.steps.github

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.VersionBumpDeterminer

class GithubPRLabelVersionBumpDeterminer implements VersionBumpDeterminer {

    def defaultBump = "patch"

    GithubPRLabelVersionBumpDeterminer(def defaultBump) {
        this.defaultBump = defaultBump
    }

    GithubPRLabelVersionBumpDeterminer() {}

    @Override
    String determineVersionDump(BuildContext buildContext) {
        def prNumber = retrievePrId(buildContext.getCommitMessage())
        if (!prNumber) {
            return "patch";
        }

        def bump = defaultBump
        def secrets = [
                [$class: 'VaultSecret', path: "secret/${buildContext.getGroup()}/tools/sonarqube", secretValues: [
                        [$class: 'VaultSecretValue', envVar: 'GITHUB_OAUTH_TOKEN', vaultKey: 'github_token']]]
        ]
        buildContext.getScriptEngine().wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
            def output = buildContext.getScriptEngine().sh("curl -X GET -H 'Authorization: token ${buildContext.getScriptEngine().env.GITHUB_OAUTH_TOKEN}' \$GITHUB_API_URL/repos/${buildContext.getGroup()}/${buildContext.getProject()}/issues/${prNumber}/labels | jq -r '.[].name' > labels.txt")
            def labels = buildContext.getScriptEngine().readFile('labels.txt').split("\\n")

            for(int i = 0; i < labels.size(); i++) {
                if(labels[i] == "patch" || labels[i] == "major" || labels[i] == "minor") {
                    bump = labels[i];
                    break;
                }
            }
        }

        return bump;
    }

    def retrievePrId(commitMsg) {
        def pr = commitMsg =~ ".*#(\\d+).*"
        pr ? pr[0][1] : null
    }
}
