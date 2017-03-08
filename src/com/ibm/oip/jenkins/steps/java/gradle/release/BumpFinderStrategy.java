package com.ibm.oip.jenkins.steps.java.gradle.release;

interface BumpFinderStrategy {
    String retrieveBump();
}