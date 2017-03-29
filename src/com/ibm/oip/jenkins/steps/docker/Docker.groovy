package com.ibm.oip.jenkins.steps.docker

import com.ibm.oip.jenkins.steps.Step

class Docker {
    public static Step CREATE_AND_PUSH_CONTAINER = new CreatePushContainer();
    public static Step CHECK_DOCKER_BUILD = new CheckDockerBuild();
}