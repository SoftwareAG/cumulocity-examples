package com.cumulocity.snmp.service.client;

import org.snmp4j.smi.VariableBinding;

public interface PduListener {
    void onVariableBindingReceived(VariableBinding variableBinding);
}
