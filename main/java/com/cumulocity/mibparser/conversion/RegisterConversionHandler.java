package com.cumulocity.mibparser.conversion;

import com.cumulocity.mibparser.model.Register;
import lombok.extern.slf4j.Slf4j;
import net.percederberg.mibble.MibSymbol;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.snmp.SnmpNotificationType;
import net.percederberg.mibble.snmp.SnmpObjectType;
import net.percederberg.mibble.snmp.SnmpTrapType;
import net.percederberg.mibble.value.ObjectIdentifierValue;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class RegisterConversionHandler {

    public List<Register> convertSnmpObjectToRegister(List<MibSymbol> mibSymbols) {
        List<Register> registerList = new ArrayList<>();
        MibValueSymbol mibValueSymbol;

        for (MibSymbol mibSymbol : mibSymbols) {
            try {
                mibValueSymbol = (MibValueSymbol) mibSymbol;
                if (mibValueSymbol.getType() instanceof SnmpObjectType) {
                    log.debug("OBJECT-TYPE found");
                    registerList.add(convertSnmpObjectTypeToRegister((SnmpObjectType) mibValueSymbol.getType(), mibValueSymbol));
                } else if (mibValueSymbol.getType() instanceof SnmpTrapType) {
                    log.debug("TRAP-TYPE found");
                    registerList.add(convertMibTrapTypeToRegister((SnmpTrapType) mibValueSymbol.getType(), mibValueSymbol));
                } else if (mibValueSymbol.getType() instanceof SnmpNotificationType) {
                    log.debug("NOTIFICATION-TYPE found");
                    registerList.add(convertMibNotificationTypeToRegister(mibValueSymbol));
                }
            } catch (ClassCastException e) {
                log.debug("MibSymbol is not of type MibValueSymbol. Do nothing. " +
                        "Iterating to next MibSymbol object");
            }
        }
        return registerList;
    }

    private Register convertSnmpObjectTypeToRegister(SnmpObjectType snmpObjectType, MibValueSymbol mibValueSymbol) {
        return createRegister(mibValueSymbol.getName(),
                mibValueSymbol.getOid().toString(),
                mibValueSymbol.getParent().getOid().toString(),
                mibValueSymbol.getChildren(),
                snmpObjectType.getDescription()
        );
    }

    private Register convertMibTrapTypeToRegister(SnmpTrapType snmpTrapType, MibValueSymbol mibValueSymbol) {
        return createRegister(
                mibValueSymbol.getName(),
                ((ObjectIdentifierValue) snmpTrapType.getEnterprise()).getSymbol().getOid().toString(),
                ((ObjectIdentifierValue) snmpTrapType.getEnterprise()).getSymbol().getParent().getOid().toString(),
                ((ObjectIdentifierValue) snmpTrapType.getEnterprise()).getSymbol().getChildren(),
                ((ObjectIdentifierValue) snmpTrapType.getEnterprise()).getSymbol().
                        getComment().replace("\n", "")
        );
    }

    private Register convertMibNotificationTypeToRegister(MibValueSymbol mibValueSymbol) {
        return createRegister(mibValueSymbol.getName(),
                mibValueSymbol.getOid().toString(),
                mibValueSymbol.getParent().getOid().toString(),
                mibValueSymbol.getChildren(),
                ((SnmpNotificationType) mibValueSymbol.getType()).getDescription().
                        replace("\n", "")
        );
    }

    private Register createRegister(String name, String oid, String parentOid,
                                    MibValueSymbol[] childOid, String description) {
        List<String> childOids = new ArrayList<>();
        for (MibValueSymbol mibVS : childOid) {
            childOids.add(mibVS.getOid().toString());
        }
        return new Register(name, oid, description, parentOid, childOids);
    }
}
