package com.ibm.oip.jenkins.steps.java.gradle;

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step;

class ShadowAssemble implements Step {

    void doStep(BuildContext buildContext) {
        buildContext.changeStage('Assemble');
        buildContext.getScriptEngine().sh("./gradlew clean shadowJar");
    }
}
