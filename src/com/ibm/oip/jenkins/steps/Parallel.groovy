package com.ibm.oip.jenkins.steps

import com.ibm.oip.jenkins.BuildContext

class Parallel extends Step {
    private Step[] steps

    Parallel(Step... steps) {
        this.steps = steps
    }

    @Override
    void doStep(BuildContext buildContext) {
        buildContext.getScriptEngine().parallel steps.collectEntries {
            ["${it.name()}": { it.doStep(buildContext) }]
        }
    }
}
