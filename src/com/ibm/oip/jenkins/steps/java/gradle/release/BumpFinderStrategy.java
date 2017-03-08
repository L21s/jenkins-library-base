package com.ibm.oip.jenkins.steps.java.gradle.release;

import java.io.Serializable;

interface BumpFinderStrategy extends Serializable {
    String retrieveBump();
}