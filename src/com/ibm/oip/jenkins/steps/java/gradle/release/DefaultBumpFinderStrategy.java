package com.ibm.oip.jenkins.steps.java.gradle.release;

public class DefaultBumpFinderStrategy implements BumpFinderStrategy {
    public String retrieveBump() {
        return "Patch";
    }
}
