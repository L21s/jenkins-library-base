package com.ibm.oip.jenkins.exceptions;

class DeploymentTriggerException extends RuntimeException {
    public DeploymentTriggerException(String msg){
        super(msg);
    }
}