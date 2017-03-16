package com.ibm.oip.jenkins.steps.deployment;

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.exceptions.DeploymentTriggerException
import com.ibm.oip.jenkins.steps.Step;

class AbstractTriggerDeployment implements Step {
    private String targetEnvironment;

    public AbstractTriggerDeployment(String targetEnvironment){
        this.targetEnvironment = targetEnvironment;
    }

    BuildContext buildContext;
    public void doStep(BuildContext buildContext) {
        buildContext.changeStage("Deployment to ${targetEnvironment}")
        def deployment = buildContext.getScriptEngine().build job: getDeploymentJobName(), parameters: [[$class: 'StringParameterValue', name: 'group', value: buildContext.group],
                                                    [$class: 'StringParameterValue', name: 'service', value: buildContext.project],
                                                    [$class: 'StringParameterValue', name: 'version', value: buildContext.version],
                                                    [$class: 'StringParameterValue', name: 'environment', value: targetEnvironment]],
                                        wait: true, propagate: false, quietPeriod: 0
        if (deployment.result == 'FAILURE'){
            throw new DeploymentTriggerException("Deployment job to target environment ${targetEnvironment} failed!");
        }
    }

    def getDeploymentJobName() {
        throw new UnsupportedOperationException("Please provide a Jenkins job name that handles deployments.")
    }
}
