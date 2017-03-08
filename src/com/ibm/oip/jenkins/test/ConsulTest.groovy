package com.ibm.oip.jenkins.test

import groovy.json.JsonSlurper
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
class ConsulTest {
    //@Test
    public void shouldCallConsul() {
        def result = new ArrayList();
        JsonSlurper slurper = new JsonSlurper();
        def swarmIp = "10.134.79.44";
        def memberKeys = new URL("http://${swarmIp}:8500/v1/kv/swarm/docker/nodes/?recurse&keys").getText();
        List asList = slurper.parseText(memberKeys);

        for(key in slurper.parseText(memberKeys)) {
            def ipWithPort = new URL("http://${swarmIp}:8500/v1/kv/$key?raw").getText().trim();
            result.add(ipWithPort.substring(0, ipWithPort.indexOf(":")));
        }

        assert result[0] == "10.134.79.44"

    }

    @Test
    public void shouldDoSomething(){

        def list = "%sonar::something%\nsomethingstupid\n%gitlab:boz%".findAll(/%(.*?)%/);

        for (int i = 0; i < list.size(); i++) {
            println list[i];
        }
    }
}