package com.ibm.oip.jenkins.steps.deployment

import com.ibm.oip.jenkins.steps.Step
import com.ibm.oip.jenkins.steps.deployment.cloudfoundry.CloudFoundryDeployment
import com.ibm.oip.jenkins.steps.deployment.docker.IBMContainerDeployment
import com.ibm.oip.jenkins.steps.deployment.docker.KubernetesDeployment

class Deployment {
    public static Step CLOUDFOUNDRY(String targetEnv) {
        return new CloudFoundryDeployment(targetEnv);
    }
    
    public static Step IBM_CONTAINER(String targetEnv) {
        return new IBMContainerDeployment(targetEnv);
    }

    public static Step KUBERNETES(String targetEnv) {
        return new KubernetesDeployment(targetEnv);
    }
}