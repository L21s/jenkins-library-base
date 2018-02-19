package com.ibm.oip.jenkins

import com.ibm.oip.jenkins.notification.Notifier
import com.ibm.oip.jenkins.notification.NullNotifier
import com.ibm.oip.jenkins.notification.SlackNotifier

import java.util.regex.Pattern

class PipelineBuilder implements Serializable {
    private Pipeline pipeline = new Pipeline();

    public PipelineBuilder forMaster() {
        pipeline.setPattern(~/^master$/);
        return this;
    }

    public PipelineBuilder forAnyButMaster() {
        pipeline.setPattern(~/^(?!^master$).*$/);
        return this;
    }

    public PipelineBuilder forBranchByName(String branchName) {
        pipeline.setPattern(~/^(?!^${branchName}$).*$/);
        return this;
    }

    public PipelineBuilder forBranchByRegex(String regex) {
        pipeline.setPattern(Pattern.compile(regex));
        return this;
    }

    public PipelineBuilder forBranchByPattern(Pattern pattern) {
        pipeline.setPattern(pattern);
        return this;
    }

    PipelineBuilder notifyOnFailure() {
        pipeline.notifier = new SlackNotifier()
        return this
    }

    PipelineBuilder notifyOnFailure(Notifier notifier) {
        pipeline.notifier = notifier
        return this
    }


    public PipelineBuilder withSteps(steps) {
       pipeline.setSteps(steps);
       return this; 
    }

    public Pipeline build() {
        return this.pipeline;
    }
}