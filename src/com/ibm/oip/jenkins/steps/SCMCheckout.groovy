package com.ibm.oip.jenkins.steps;

import com.ibm.oip.jenkins.BuildContext;

class SCMCheckout implements Step {
    @NonCPS
    public void doStep(BuildContext buildContext) {
        buildContext.changeStage("Checkout");
        buildContext.getScriptEngine().checkout buildContext.getScriptEngine().scm

        buildContext.addGitProperties();
    }
}