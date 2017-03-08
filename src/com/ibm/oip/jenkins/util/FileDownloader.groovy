package com.ibm.oip.jenkins.util

/**
 * Created by tobiaslarscheid on 07.11.16.
 */

import com.ibm.oip.jenkins.BuildContext;

class FileDownloader {

    private final BuildContext buildContext;

    private static final String TMP_FOLDER = "fileDownload";

    FileDownloader(BuildContext buildContext) {
        this.buildContext = buildContext;
    }

    public <V> void withGit(String repoUrl = DEFAULT_REPO_URL, String repoBranch = DEFAULT_BRANCH,
                        String credentialsId = null, labelExpression = '', Closure<V> body) {

        buildContext.getScriptEngine().dir(TMP_FOLDER) {
            // Flush the directory
            buildContext.getScriptEngine().deleteDir()

            // Checkout
            buildContext.getScriptEngine().echo "Checking out ${repoUrl}, branch=${repoBranch}"
            buildContext.getScriptEngine().checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: repoBranch]],
                                                                                         userRemoteConfigs: [[credentialsId: credentialsId, url: repoUrl]]]

            // Invoke body in the folder
            body();

            // Flush the directory again
            buildContext.getScriptEngine().deleteDir()
        }
    }
}

