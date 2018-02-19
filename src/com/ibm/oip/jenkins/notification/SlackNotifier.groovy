package com.ibm.oip.jenkins.notification;

import com.ibm.oip.jenkins.BuildContext

class SlackNotifier implements Notifier, Serializable {
    String channel;

    public SlackNotifier(String channel) {
        this.channel = channel;
    }

    public SlackNotifier() { }

    void notifyFailure(BuildContext buildContext) {
        if(channel != null) {
            buildContext.getScriptEngine().slackSend (color: "#ff0000", channel: channel, message:  generateMessage(buildContext))
        } else {
            buildContext.getScriptEngine().slackSend (color: "#ff0000", message:  generateMessage(buildContext))
        }
    }

    private static String generateMessage(BuildContext buildContext) {
        if(!buildContext.getCommitId()) {
            buildContext.setCommitId("=NOIDYET=")
        }
        return "*Build failed on project*: " +
                "${buildContext.getJobName()} (<${buildContext.getBuildUrl()}/console|${buildContext.getDisplayName()}>). " +
                "\n*Branch* ${buildContext.getBranch()}" +
                "\n*Commit* ${buildContext.getCommitId()[0..7]}" +
                "\n*Commit Message*: ${buildContext.getCommitMessage()}" +
                "\n*From*: ${buildContext.getAuthor()}" +
                "\n*Error in stage*: ${buildContext.getStage()}"
    }
}