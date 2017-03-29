package com.ibm.oip.jenkins.steps.docker

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step;

class CheckDockerBuild implements Step {
    BuildContext buildContext;

    public void doStep(BuildContext buildContext) {
        this.buildContext = buildContext;
        buildContext.changeStage('Check: Container-Build');
        buildContext.getScriptEngine().sh "docker build -t ${buildContext.getCommitId()} .";
        buildContext.getScriptEngine().sh "docker rmi ${buildContext.getCommitId()}"
    }
}