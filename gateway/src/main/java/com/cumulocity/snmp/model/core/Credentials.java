package com.cumulocity.snmp.model.core;

public interface Credentials extends TenantProvider {
    String getTenant();
    String getName();
    String getPassword();
}
