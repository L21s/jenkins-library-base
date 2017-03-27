package com.ibm.oip.jenkins.steps.java.gradle;

import com.ibm.oip.jenkins.BuildContext

abstract class AbstractGradleStep implements Step {

    void doGradleStep(BuildContext ctx, String gradleCommand) {
        buildContext.getScriptEngine().withCredentials([
                [$class: 'UsernamePasswordMultiBinding', credentialsId: ctx.getGroup() + '-nexus', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            buildContext.getScriptEngine().sh("./gradlew ${gradleCommand} -PrepositoryUsername=${buildContext.getScriptEngine().env.USERNAME} -PrepositoryPassword=${buildContext.getScriptEngine().env.PASSWORD}");
        }
    }
}
