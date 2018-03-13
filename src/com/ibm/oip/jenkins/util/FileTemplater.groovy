package com.ibm.oip.jenkins.util

import com.ibm.oip.jenkins.BuildContext

class FileTemplater implements Serializable {
    private BuildContext buildContext;
    private String filePath;

    public FileTemplater(BuildContext buildContext, String filePath){
        this.buildContext = buildContext;
        this.filePath = filePath;
    }

    public void template(searchString, replacement) {
        buildContext.getScriptEngine().sh "find $filePath -type f -exec sed -i.bak 's|$searchString|$replacement|g' {} \\;";
    }

    public void deleteLinesMatching(searchString) {
        buildContext.getScriptEngine().sh "sed -i.bak '/$searchString/d' $filePath";
    }
}
