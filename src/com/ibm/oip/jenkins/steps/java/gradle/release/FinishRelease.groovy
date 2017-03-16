package com.ibm.oip.jenkins.steps.java.gradle.release

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step

class FinishRelease implements Step {
    void doStep(BuildContext buildContext) {
        buildContext.changeStage('Publish release');
        buildContext.getScriptEngine().sh ("git push --tags");
        buildContext.getScriptEngine().sh ("./gradlew publish");
    }
}
