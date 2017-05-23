package com.ibm.oip.jenkins.steps

import com.ibm.oip.jenkins.BuildContext

interface VersionBumpDeterminer extends Serializable{
    public String determineVersionDump(BuildContext buildContext)
}