package com.ibm.oip.jenkins.steps

import com.ibm.oip.jenkins.environments.TargetEnvironment
import com.ibm.oip.jenkins.steps.deployment.AbstractTriggerDeployment;

class Common {
    public static Step CHECKOUT = new SCMCheckout();

    public static Step CHECKOUT_TAG(String tag) {
        return new Checkout("refs/tags/$tag");
    }

    public static Step CHECKOUT_COMMIT(String commitId) {
        return new Checkout(commitId);
    }
}