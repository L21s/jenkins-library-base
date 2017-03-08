package com.ibm.oip.jenkins

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

    public PipelineBuilder withSteps(steps) {
       pipeline.setSteps(steps);
       return this; 
    }

    public Pipeline build() {
        return this.pipeline;
    }
}