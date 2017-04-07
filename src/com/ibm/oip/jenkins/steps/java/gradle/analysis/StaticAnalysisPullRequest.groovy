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

            def gradleOutput;
            def coverageResult;
            def coverageTargetFailed = false;
            try {
                gradleOutput = doGradleStepReturnOutput(buildContext, "jacocoTestCoverageVerification");
            } catch(err) {
                coverageResult = extractCoverage(gradleOutput);

                if(coverageResult == null) {
                    throw new RuntimeException("Could not extract coverage information, but coverage target failed")
                }
                coverageTargetFailed = true;
            }

            def status = new GithubStatus();
            status.target_url = buildContext.jobUrl + "Coverage_Report";
            status.context = "code-coverage"
            if(coverageTargetFailed) {
                status.description = "Coverage was: ${coverageResult.result * 100 }% (Target: ${coverageResult.target * 100}%) - failing.";
                status.state = "failure";
            } else {
                status.description = "Code coverage looks good - success.";
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

    static class CoverageResult implements  Serializable {
        def result;

        def target;
    }

    @NonCPS
    def extractCoverage(gradleOutput) {
        def matcher = gradleOutput =~ "> Rule violated for bundle dropwizard-sample: instructions covered ratio is (\\d.\\d+), but expected minimum is (\\d.\\d+)";

        if(!matcher) {
            return null;
        }

        CoverageResult result = new CoverageResult();
        result.result = Double.valueOf(matcher[0][1]);
        result.target = Double.valueOf(matcher[0][2]);
        return result;
    }
}
