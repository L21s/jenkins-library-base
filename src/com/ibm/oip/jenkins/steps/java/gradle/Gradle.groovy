package com.ibm.oip.jenkins.steps.java.gradle

import com.ibm.oip.jenkins.steps.Step
import com.ibm.oip.jenkins.steps.java.gradle.release.FinishRelease
import com.ibm.oip.jenkins.steps.java.gradle.release.PrepareRelease
import com.ibm.oip.jenkins.steps.java.gradle.analysis.*

class Gradle implements Serializable {
    public static Step ASSEMBLE = new Assemble();
    public static Step SHADOW_ASSEMBLE = new GradleAssemble();
    public static Step PREPARE_RELEASE = new PrepareRelease();
    public static Step UNIT_TEST = new UnitTest();
    public static Step INTEGRATION_TEST = new IntegrationTest();
    public static Step FINISH_RELEASE = new FinishRelease();
    public static Step STATIC_ANALYSIS = new StaticAnalysis();
    public static Step STATIC_ANALYSIS_PR = new StaticAnalysisPullRequest();
}
