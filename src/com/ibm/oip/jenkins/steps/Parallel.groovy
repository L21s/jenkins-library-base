package com.ibm.oip.jenkins.steps

import com.ibm.oip.jenkins.BuildContext

class Parallel implements Step {
    private Step[] steps

    Parallel(Step... steps) {
        this.steps = steps
    }

    @Override
    void doStep(BuildContext buildContext) {
        def stepCount = 0
        buildContext.getScriptEngine().parallel steps.collectEntries {
            [(stepCount++): { it.doStep(buildContext) }]
        }
    }
}
