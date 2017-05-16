package com.ibm.oip.jenkins

class Pipeline implements Serializable {
    def steps;

    def pattern = ~/^.*$/;

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