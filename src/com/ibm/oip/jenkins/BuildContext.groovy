package com.ibm.oip.jenkins

class BuildContext implements Serializable {
    def scriptEngine;

    def group;

    def project;

    def commitId;

    def pullRequestCommitId;

    def commitMessage;

    def author;

    def jobName;

    def buildUrl;

    def jobUrl;

    def displayName;
    
    def customProperties;

    def stage;

    def version;

    def branch;

    public static BuildContext create(scriptEngine, customProperties, branch) {
        BuildContext result = new BuildContext();
        result.scriptEngine = scriptEngine;
        result.group = customProperties["group"]
        result.project = customProperties["project"]
        result.version = customProperties["version"]
        result.jobName = scriptEngine.env.JOB_NAME;
        result.buildUrl = scriptEngine.env.BUILD_URL;
        result.jobUrl = scriptEngine.env.JOB_URL;
        result.displayName = scriptEngine.currentBuild.displayName;
        result.customProperties = customProperties;
        result.branch = branch;
        return result;
    }

    public void addGitProperties() {
        this.commitId = retrieveCommitId(scriptEngine);
        this.commitMessage = retrieveCommitMessage(scriptEngine);
        this.author = retrieveAuthor(scriptEngine, commitId);
        this.pullRequestCommitId = retrieveLastCommitOfPullRequest(scriptEngine);
    }

    def changeStage(stage) {
        this.stage = stage;
        this.scriptEngine.env.STAGE = stage;
        this.scriptEngine.stage stage;
    }

    private static String retrieveCommitId(scriptEngine) {
        return scriptEngine.sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
    }

    private static String retrieveLastCommitOfPullRequest(scriptEngine) {
        return scriptEngine.sh(returnStdout: true, script: 'git rev-parse @~').trim()
    }

    private static String retrieveCommitMessage(scriptEngine) {
        return scriptEngine.sh(returnStdout: true, script: 'git log -1 --pretty=%B').trim()
    }

    private static String retrieveAuthor(scriptEngine, commitId) {
        return scriptEngine.sh(returnStdout: true, script: "git --no-pager show -s --format='%an' $commitId").trim()
    }   
}