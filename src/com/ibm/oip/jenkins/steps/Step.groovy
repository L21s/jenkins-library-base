package com.ibm.oip.jenkins.steps

import com.ibm.oip.jenkins.BuildContext

interface Step extends Serializable {
    public void doStep(BuildContext buildContext);
}