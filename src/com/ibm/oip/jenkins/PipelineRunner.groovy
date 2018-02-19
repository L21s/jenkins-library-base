package com.ibm.oip.jenkins

import com.ibm.fss.iip.jenkins.notification.*
import com.ibm.oip.jenkins.notification.Notifier
import com.ibm.oip.jenkins.notification.SlackNotifier

class PipelineRunner  implements Serializable {
    private Pipeline[] pipelines; 

    private Object scriptEngine;

    private String branch;

    private Object env;

    private Object nodeLabel;

    def customProperties = [];

    public PipelineRunner() {
    }

    public PipelineRunner withPossiblePipelines(Pipeline... pipelines) {
        this.pipelines = pipelines;
        return this;
    }

    public PipelineRunner withScriptEngine(Object scriptEngine) {
        this.scriptEngine = scriptEngine;
        return this;
    }

    public PipelineRunner withProperties(customProperties) {
        this.customProperties = customProperties;
        return this;
    }  

    public PipelineRunner withBranch(String branch) {
        this.branch = branch;
        return this;
    }  

    public PipelineRunner withEnv(Object env) {
        this.env = env;
        return this;
    }

    public PipelineRunner onNode(Object nodeLabel) {
        this.nodeLabel = nodeLabel;
        return this;
    }

    public void run() {
        def buildContext;

        for(int i = 0; i < this.pipelines.length; i++) {
            scriptEngine.println "bbbb ${this.pipelines.length}"
            if(this.pipelines[i].canBuild(branch)) {
                try {
                    buildContext = BuildContext.create(scriptEngine, customProperties, branch);
                    buildContext.setNodeLabel(this.nodeLabel);
                    this.pipelines[i].run(buildContext);
                } catch(err) {
                    this.pipelines[i].notifier.notifyFailure(buildContext)
                    buildContext.getScriptEngine().currentBuild.result = 'FAILURE';
                    throw err;
                }
                return;
            }
        }
    }
}
