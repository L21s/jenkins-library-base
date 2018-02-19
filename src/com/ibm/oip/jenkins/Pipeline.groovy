package com.ibm.oip.jenkins

import com.ibm.oip.jenkins.notification.Notifier
import com.ibm.oip.jenkins.notification.NullNotifier

class Pipeline implements Serializable {
    def steps;

    def pattern = ~/^.*$/;

    protected Notifier notifier = new NullNotifier();

    public boolean canBuild(String branch) {
        return pattern.matcher(branch).matches();
    }

    public void run(buildContext) {
        buildContext.getScriptEngine().node(buildContext.nodeLabel) {
            buildContext.getScriptEngine().ws(buildContext.getProject() + "-workspace") {
                buildContext.getScriptEngine().configFileProvider(
                        [buildContext.getScriptEngine().configFile(fileId: "${buildContext.getGroup()}-config", variable: 'PROJECT_CONFIG')]) {
                    def variables = buildContext.getScriptEngine().load buildContext.getScriptEngine().env.PROJECT_CONFIG
                    buildContext.getScriptEngine().withEnv(variables) {
                        buildContext.getScriptEngine().println "aaaa " + this.steps.size
                        buildContext.getScriptEngine().deleteDir();
                        for (int i = 0; i < this.steps.size(); i++) {
                            this.steps[i].doStep(buildContext);
                        }
                    }
                }
            }
        }
    }
}