package com.ibm.oip.jenkins.steps

import com.ibm.oip.jenkins.BuildContext

import java.util.concurrent.atomic.AtomicInteger

abstract class Step implements Serializable {
    // no Traits support in Jenkins yet (https://issues.jenkins-ci.org/browse/JENKINS-46145)
    String name() { this.class.name + " (" + StepCounter.next() + ")" }

    abstract void doStep(BuildContext buildContext)

    static class StepCounter {
        private static final AtomicInteger counter = new AtomicInteger(0)

        static int next() { counter.incrementAndGet() }
    }
}