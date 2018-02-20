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

    def nodeLabel;

    public static BuildContext create(scriptEngine, customProperties, branch) {
        BuildContext result = new BuildContext();
        result.scriptEngine = scriptEngine;
        result.version = customProperties["version"]
        result.group = customProperties["group"]
        result.project = customProperties["project"]
        result.jobName = scriptEngine.env.JOB_NAME;
        result.buildUrl = scriptEngine.env.BUILD_URL;
        result.jobUrl = scriptEngine.env.JOB_URL;
        result.displayName = scriptEngine.currentBuild.displayName;
        result.customProperties = customProperties;
        result.branch = branch;

        if(!result.group || !result.project) {
            // retrieve org and project
            def url = scriptEngine.scm.getUserRemoteConfigs()[0].getUrl()
            def parts = url =~ ".*\\/(.*?)\\/(.*?)\\.git" // Github Regex
            result.setGroup(parts[0][1])
            result.setProject(parts[0][2])
        }

        return result;
    }

    public void addGitProperties() {
        this.commitId = retrieveCommitId(scriptEngine);
        this.commitMessage = retrieveCommitMessage(scriptEngine);
        this.author = retrieveAuthor(scriptEngine, commitId);
        this.pullRequestCommitId = retrieveLastCommitOfPullRequest(scriptEngine);
    }

    def changeStage(String stage, Closure stageClosure) {
        this.stage = stage;
        this.scriptEngine.env.STAGE = stage
        stageClosure.setDelegate(this.scriptEngine.stage)
        this.scriptEngine.stage(stage, stageClosure)
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