package com.ibm.oip.jenkins.steps

import com.ibm.oip.jenkins.steps.*;
import com.ibm.oip.jenkins.steps.deployment.TriggerDeployment

class Common {
    public static Step CHECKOUT = new SCMCheckout();

    public static Step TRIGGER_DEPLOYMENT(String targetEnv) {
        return new TriggerDeployment(targetEnv);
    }
}