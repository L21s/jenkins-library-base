package com.ibm.oip.jenkins.notification

import com.ibm.oip.jenkins.BuildContext

class NullNotifier implements  Notifier {
    @Override
    void notifyFailure(BuildContext buildContext) {
        buildContext.getScriptEngine().println "notifiy"
        // nothing to do
    }
}
