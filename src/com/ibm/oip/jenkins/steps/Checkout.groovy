package com.ibm.oip.jenkins.steps;

import com.ibm.oip.jenkins.BuildContext;

class Checkout extends Step {
    void doStep(BuildContext buildContext) {
        buildContext.changeStage("Checkout");
        buildContext.getScriptEngine().checkout([$class           : 'GitSCM',
                                                 branches         : [[name: "refs/heads/enhancement/kubernetes"]],
                                                 userRemoteConfigs: [[credentialsId: 'AOK-FAMI-github', url: "https://github.gcloud.eu-de.bluemix.net/${env.GROUP}/${env.SERVICE}.git"]],
                                                 extensions       : [[$class: 'LocalBranch', localBranch: "enhancement/kubernetes"]]]) {

        }
        buildContext.addGitProperties();
    }
}