package com.cumulocity.agent.snmp.device;

import java.util.Random;

import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.Variable;

public abstract class SnmpTrapSender {

    protected static final int port = 6671;

    protected static final String community = "public";

    protected String ipAddress = "127.0.0.1";

    protected String trapOid = "1.3.6.1.2.1.34.4.0.2";

    private Variable variable = null;

    protected Variable getVariable() {
        if (variable == null) {
            Random random = new Random();
            return new Counter32(random.nextInt(100));
        } else {
            return variable;
        }
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setTrapOid(String trapOid) {
        this.trapOid = trapOid;
    }

    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    /**
     * This methods sends the V1/V2 trap
     * @param version
     */
    public abstract void sendSnmpV1V2Trap(int version);

    /**
     * Sends the v3 trap
     */
    public abstract void sendSnmpV3Trap();
}
