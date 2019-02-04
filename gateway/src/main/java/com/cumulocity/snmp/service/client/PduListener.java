package com.cumulocity.snmp.service.client;

import org.snmp4j.PDU;

public interface PduListener {
    void onPduRecived(PDU pdu);
}
