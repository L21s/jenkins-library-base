package com.ibm.oip.jenkins.steps;

import com.ibm.oip.jenkins.BuildContext;

class SCMCheckout implements Step {
    @NonCPS
    public void doStep(BuildContext buildContext) {
        buildContext.changeStage("Checkout");
        buildContext.getScriptEngine().checkout buildContext.getScriptEngine().scm

        buildContext.addGitProperties();

        // retrieve org and project
        def url = buildContext.getScriptEngine().scm.getUserRemoteConfigs()[0].getUrl()
        def parts = url =~ ".*\\/(.*?)\\/(.*?)\\.git"
        buildContext.setGroup(parts[0][1])
        buildContext.setProject(parts[0][2])
    }
}