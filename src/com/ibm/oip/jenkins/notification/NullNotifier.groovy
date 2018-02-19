package com.ibm.oip.jenkins.notification

import com.ibm.oip.jenkins.BuildContext

class NullNotifier implements  Notifier {
    @Override
    void notifyFailure(BuildContext buildContext) {
        // nothing to do
    }
}
