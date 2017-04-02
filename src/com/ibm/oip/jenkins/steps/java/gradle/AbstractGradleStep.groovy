package com.ibm.oip.jenkins.steps.java.gradle;

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.steps.Step

abstract class AbstractGradleStep implements Step {

    void doGradleStep(BuildContext buildContext, String gradleCommand) {
        buildContext.getScriptEngine().withCredentials([
                [$class: 'UsernamePasswordMultiBinding', credentialsId: buildContext.getGroup() + '-nexus', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            buildContext.getScriptEngine().sh("./gradlew ${gradleCommand} -PrepositoryUsername=${buildContext.getScriptEngine().env.USERNAME} -PrepositoryPassword=${buildContext.getScriptEngine().env.PASSWORD} -PnexusUsername=${buildContext.getScriptEngine().env.USERNAME} -PnexusPassword=${buildContext.getScriptEngine().env.PASSWORD}");
        }
    }
}
