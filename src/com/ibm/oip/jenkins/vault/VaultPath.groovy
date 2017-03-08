package com.ibm.oip.jenkins.vault

import com.ibm.oip.jenkins.environments.TargetEnvironment

class VaultPath implements Serializable {
    def path;

    def value;


    public static VaultPath createServicePath(buildContext, TargetEnvironment targetEnvironment, def credential) {
        return createPathInternal(createDefaultServicePath(buildContext, targetEnvironment), credential);
    }

    public static VaultPath createToolPath(def credential) {
        return createPathInternal("secret/tools/", credential);
    }

    private static VaultPath createPathInternal(basePath, credential) {
        VaultPath instance = new VaultPath();

        if (credential.indexOf("::") == -1) {
            instance.path = basePath + credential;
            instance.value = "value";
            return instance;
        }
        instance.path = basePath + credential.substring(0, credential.indexOf("::"))
        instance.value = credential.substring(credential.indexOf("::") + 2, credential.length())
        return instance;
    }

    private static String createDefaultServicePath(buildContext, targetEnvironment) {
        return "secret/services/${targetEnvironment.getName()}/${buildContext.getGroup()}/${buildContext.getProject()}/"
    }

}