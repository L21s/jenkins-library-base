package com.ibm.oip.jenkins.steps;

import com.ibm.oip.jenkins.BuildContext;

class SCMCheckout implements Step {
    public void doStep(BuildContext buildContext) {
        buildContext.changeStage("Checkout");
        buildContext.getScriptEngine().checkout([
                $class: 'GitSCM',
                branches: buildContext.getScriptEngine().scm.branches,
                extensions: buildContext.getScriptEngine().scm.extensions + [[$class: 'LocalBranch', localBranch: 'master']],
                userRemoteConfigs: buildContext.getScriptEngine().scm.userRemoteConfigs
        ])

        buildContext.addGitProperties();
    }
}