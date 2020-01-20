package com.cumulocity.agent.snmp.cucumber.model;

import lombok.Data;

@Data
public class SnmpDeviceAuthentication {

    private String username;
    private String authPassword;
    private String privPassword;
    private int authProtocol;
    private int privProtocol;
    private int securityLevel;
    private String engineId;
}
