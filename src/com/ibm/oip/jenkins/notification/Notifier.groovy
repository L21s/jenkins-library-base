package com.ibm.oip.jenkins.notification

import com.ibm.oip.jenkins.BuildContext

interface Notifier {
    void notifyFailure(BuildContext buildContext)
}
