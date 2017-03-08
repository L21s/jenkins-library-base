package com.ibm.oip.jenkins.steps.java.gradle;

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step;

class Assemble implements Step {

    void doStep(BuildContext buildContext) {
        buildContext.changeStage('Assemble');
        buildContext.getScriptEngine().gitlabCommitStatus("assemble") {
            buildContext.getScriptEngine().sh("./gradlew clean assemble");
        }
    }
}