package com.cumulocity.snmp.model.core;

public interface Credentials extends HasTenant {
    String getTenant();
    String getName();
    String getPassword();
}
