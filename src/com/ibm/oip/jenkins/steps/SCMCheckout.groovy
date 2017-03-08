package com.ibm.oip.jenkins.steps;

import com.ibm.oip.jenkins.BuildContext;

class SCMCheckout implements Step {
    public void doStep(BuildContext buildContext) {
        buildContext.changeStage("Checkout");
        buildContext.getScriptEngine().checkout buildContext.getScriptEngine().scm
        // buildContext.getScriptEngine().sh "git config --global user.email 'jenkins@${buildContext.devstack.getBaseUrl()}'"
        //buildContext.getScriptEngine().sh 'git config --global user.name "jenkins"'
        buildContext.addGitProperties();
    }
}