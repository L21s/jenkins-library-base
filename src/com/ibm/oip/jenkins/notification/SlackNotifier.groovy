package com.ibm.oip.jenkins.notification;

import com.ibm.oip.jenkins.BuildContext

class SlackNotifier implements Serializable {
    private BuildContext buildContext;

    public void notifyFailure(BuildContext buildContext) {
        this.buildContext = buildContext;

        if(!buildContext.getCommitId()) {
            buildContext.setCommitId("=NOIDYET=")
        }
        buildContext.getScriptEngine().slackSend (color: "#ff0000", message:  "Build failed on project: " +
                "${buildContext.getJobName()} (<${buildContext.getBuildUrl()}/console|${buildContext.getDisplayName()}>). " +
                "*Commit* ${buildContext.getCommitId()[0..7]}" +
                "\n*Commit Message*: ${buildContext.getCommitMessage()}" +
                "\n*From*: ${buildContext.getAuthor()}" +
                "\n*Error in stage*: ${buildContext.getStage()}")
    }
}
