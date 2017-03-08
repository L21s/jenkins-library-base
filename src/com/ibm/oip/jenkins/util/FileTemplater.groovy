package com.ibm.oip.jenkins.util

import com.ibm.oip.jenkins.BuildContext
import com.ibm.oip.jenkins.vault.VaultPath;

class FileTemplater implements Serializable {
    private BuildContext buildContext;
    private String filePath;

    public FileTemplater(BuildContext buildContext, String filePath){
        this.buildContext = buildContext;
        this.filePath = filePath;
    }

    public void template(searchString, replacement) {
        buildContext.getScriptEngine().sh "sed -i.bak 's|$searchString|$replacement|g' $filePath";
    }

    public void templateCredential(searchString, VaultPath vaultPath) {
        buildContext.getScriptEngine().wrap([$class: 'VaultBuildWrapper', vaultSecrets: [
                [$class: 'VaultSecret', path: vaultPath.getPath(), secretValues: [
                        [$class: 'VaultSecretValue', envVar: 'SECRET', vaultKey: vaultPath.getValue()]]]]]) {
            template(searchString, buildContext.scriptEngine.env.SECRET);
        }
    }

    public void deleteLinesMatching(searchString) {
        buildContext.getScriptEngine().sh "sed -i.bak '/$searchString/d' $filePath";
    }
}
