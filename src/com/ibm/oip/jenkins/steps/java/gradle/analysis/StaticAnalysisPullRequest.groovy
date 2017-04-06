package com.ibm.oip.jenkins.steps.java.gradle.analysis

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.java.gradle.AbstractGradleStep
import groovy.json.JsonOutput

class StaticAnalysisPullRequest extends AbstractGradleStep {
    static class GithubStatus implements Serializable {
        String state;
        String target_url;
        String description;
        String context;
    }

    void doStep(BuildContext buildContext) {
        buildContext.changeStage('Static analysis');
        def secrets = [
                [$class: 'VaultSecret', path: "secret/${buildContext.getGroup()}/tools/sonarqube", secretValues: [
                        [$class: 'VaultSecretValue', envVar: 'SONARQUBE_TOKEN', vaultKey: 'api_token'],
                        [$class: 'VaultSecretValue', envVar: 'GITHUB_OAUTH_TOKEN', vaultKey: 'github_token']]]
        ]
        buildContext.getScriptEngine().wrap([$class: 'VaultBuildWrapper', vaultSecrets: secrets]) {
            def prNumber = buildContext.branch.replace("PR-", "");
            doGradleStep(buildContext, "sonarqube " +
                    "-Dsonar.analysis.mode=preview " +
                    "-Dsonar.github.pullRequest=${prNumber} " +
                    "-Dsonar.github.repository=${buildContext.group}/${buildContext.project} " +
                    "-Dsonar.github.oauth=${buildContext.getScriptEngine().env.GITHUB_OAUTH_TOKEN} " +
                    "-Dsonar.host.url=\$SONARQUBE_URL " +
                    "-Dsonar.login=${buildContext.getScriptEngine().env.SONARQUBE_TOKEN} ")

            // Code Coverage
            doGradleStep(buildContext, "coverageTestReport")
            buildContext.getScriptEngine().publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "build/jacocoHtml", reportFiles: 'index.html', reportName: 'Coverage Report'])

            def htmlReport = buildContext.getScriptEngine().readFile 'build/jacocoHtml/index.html';
            def overallCoverageText = extractCoverage(htmlReport)
            def overallCoverage = Integer.parseInt(overallCoverageText.substring(0, overallCoverageText.length() - 1))

            def target;
            if (buildContext.getScriptEngine().fileExists('.coverage')) {
                target = Integer.parseInt(buildContext.getScriptEngine().readFile('.coverage').trim())
            } else {
                target = 80;
            }

            def status = new GithubStatus();
            status.target_url = buildContext.jobUrl + "Coverage_Report";
            status.context = "code-coverage"
            if (overallCoverage < target) {
                status.description = "Coverage was: ${overallCoverage}% (Target: ${target}%) - failing.";
                status.state = "failure";

            } else {
                status.description = "Coverage was: ${overallCoverage}% (Target: ${target}%) - success.";
                status.state = "success";
            }

            notifyGithubCoverageStatus(buildContext, prNumber, status)
        }
    }

    def retrieveGithubCommitId(buildContext, prNumber) {
        return buildContext.getScriptEngine().sh(returnStdout: true, script: "curl -s -X GET " +
                "-H \"Authorization: token ${buildContext.getScriptEngine().env.GITHUB_OAUTH_TOKEN}\" " +
                "\$GITHUB_API_URL/repos/${buildContext.group}/${buildContext.project}/pulls/${prNumber} | jq -r '.head.sha'").trim();
    }

    def notifyGithubCoverageStatus(buildContext, prNumber, status) {
        buildContext.getScriptEngine().writeFile file: "githubRequest", text: JsonOutput.toJson(status)
        buildContext.getScriptEngine().sh "curl -X POST --data @githubRequest " +
                "-H \"Authorization: token ${buildContext.getScriptEngine().env.GITHUB_OAUTH_TOKEN}\" " +
                "\$GITHUB_API_URL/repos/${buildContext.group}/${buildContext.project}/statuses/${retrieveGithubCommitId(buildContext, prNumber)}";
    }

    def extractCoverage(htmlReport) {
        XmlSlurper slurper = new XmlSlurper();
        slurper.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
        slurper.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        return slurper.parseText(htmlReport).body.table.tfoot.tr.td[2].text();
    }
}
