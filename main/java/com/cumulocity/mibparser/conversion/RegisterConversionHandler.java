package com.cumulocity.mibparser.conversion;

import com.cumulocity.mibparser.model.Register;
import lombok.extern.slf4j.Slf4j;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.snmp.SnmpNotificationType;
import net.percederberg.mibble.snmp.SnmpObjectType;
import net.percederberg.mibble.snmp.SnmpTrapType;
import net.percederberg.mibble.snmp.SnmpType;
import net.percederberg.mibble.value.ObjectIdentifierValue;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class RegisterConversionHandler {

    public Register convertSnmpObjectToRegister(MibValueSymbol mibValueSymbol) {
        if (mibValueSymbol.getType() instanceof SnmpObjectType) {
            log.debug("OBJECT-TYPE found");
            return convertSnmpObjectTypeToRegister((SnmpObjectType) mibValueSymbol.getType(), mibValueSymbol);
        } else if (mibValueSymbol.getType() instanceof SnmpTrapType) {
            log.debug("TRAP-TYPE found");
            return convertMibTrapTypeToRegister((SnmpTrapType) mibValueSymbol.getType(), mibValueSymbol);
        } else if (mibValueSymbol.getType() instanceof SnmpNotificationType) {
            log.debug("NOTIFICATION-TYPE found");
            return convertMibNotificationTypeToRegister(mibValueSymbol);
        }
        return null;
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
