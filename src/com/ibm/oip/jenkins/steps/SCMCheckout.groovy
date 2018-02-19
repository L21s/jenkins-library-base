package com.ibm.oip.jenkins.steps;

import com.ibm.oip.jenkins.BuildContext;

class SCMCheckout extends Step {
    public void doStep(BuildContext buildContext) {
        buildContext.changeStage("Checkout");
        buildContext.getScriptEngine().checkout([
                $class: 'GitSCM',
                branches: buildContext.getScriptEngine().scm.branches,
                extensions: [[$class: 'LocalBranch', localBranch: buildContext.branch], [$class: 'CloneOption', depth: 0, noTags: false, reference: '', shallow: false]],
                userRemoteConfigs: buildContext.getScriptEngine().scm.userRemoteConfigs
        ])

        buildContext.addGitProperties();
    }
}