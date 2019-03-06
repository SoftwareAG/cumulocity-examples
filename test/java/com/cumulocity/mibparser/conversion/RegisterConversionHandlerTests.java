package com.cumulocity.mibparser.conversion;

import com.cumulocity.mibparser.model.Register;
import net.percederberg.mibble.MibSymbol;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.snmp.SnmpNotificationType;
import net.percederberg.mibble.snmp.SnmpObjectType;
import net.percederberg.mibble.snmp.SnmpTrapType;
import net.percederberg.mibble.value.ObjectIdentifierValue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RegisterConversionHandlerTests {

    Iterator<MibSymbol> iterator;
    List<MibSymbol> mibSymbols;
    MibSymbol mibSymbol;
    @InjectMocks
    private RegisterConversionHandler handler;

    @Before
    public void setup() {
        iterator = mock(Iterator.class);
        mibSymbols = mock(List.class);
        mibSymbol = mock(MibSymbol.class);

        when(mibSymbols.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, false);
    }

    @Test
    public void shouldReturnDummyRegisterObjectForSnmpType() {
        when(iterator.next()).thenReturn(mibSymbol);

        List<Register> register = handler.convertSnmpObjectToRegister(mibSymbols);

        Assert.assertNotNull(register);
    }

    @Test
    public void shouldReturnRegisterObjectForSnmpObjectType() {
        MibValueSymbol mibValueSymbol = mock(MibValueSymbol.class);
        SnmpObjectType snmpObjectType = mock(SnmpObjectType.class);
        ObjectIdentifierValue objectIdentifierValue = mock(ObjectIdentifierValue.class);
        when(iterator.next()).thenReturn(mibValueSymbol);

        when(mibValueSymbol.getType()).thenReturn(snmpObjectType);
        when(mibValueSymbol.getName()).thenReturn("");
        when(mibValueSymbol.getOid()).thenReturn(objectIdentifierValue);
        when(mibValueSymbol.getParent()).thenReturn(mibValueSymbol);
        when(mibValueSymbol.getParent().getOid()).thenReturn(objectIdentifierValue);
        when(mibValueSymbol.getChildren()).thenReturn(new MibValueSymbol[]{});
        when(snmpObjectType.getDescription()).thenReturn("");

        List<Register> register = handler.convertSnmpObjectToRegister(mibSymbols);

        Assert.assertNotNull(register);
    }

    @Test
    public void shouldReturnRegisterObjectForSnmpTrapType() {
        MibValueSymbol mibValueSymbol = mock(MibValueSymbol.class);
        SnmpTrapType snmpTrapType = mock(SnmpTrapType.class);
        ObjectIdentifierValue objectIdentifierValue = mock(ObjectIdentifierValue.class);
        when(iterator.next()).thenReturn(mibValueSymbol);

        when(mibValueSymbol.getType()).thenReturn(snmpTrapType);
        when(mibValueSymbol.getName()).thenReturn("");
        when(snmpTrapType.getEnterprise()).thenReturn(objectIdentifierValue);
        when(objectIdentifierValue.getSymbol()).thenReturn(mibValueSymbol);
        when(mibValueSymbol.getOid()).thenReturn(objectIdentifierValue);
        when(mibValueSymbol.getParent()).thenReturn(mibValueSymbol);
        when(mibValueSymbol.getParent().getOid()).thenReturn(objectIdentifierValue);
        when(mibValueSymbol.getChildren()).thenReturn(new MibValueSymbol[]{});
        when(mibValueSymbol.getComment()).thenReturn("");

        List<Register> register = handler.convertSnmpObjectToRegister(mibSymbols);

        Assert.assertNotNull(register);
    }

    @Test
    public void shouldReturnRegisterObjectForSnmpNotificationType() {
        MibValueSymbol mibValueSymbol = mock(MibValueSymbol.class);
        SnmpNotificationType snmpNotificationType = mock(SnmpNotificationType.class);
        ObjectIdentifierValue objectIdentifierValue = mock(ObjectIdentifierValue.class);
        when(iterator.next()).thenReturn(mibValueSymbol);

        when(mibValueSymbol.getType()).thenReturn(snmpNotificationType);
        when(mibValueSymbol.getName()).thenReturn("");
        when(mibValueSymbol.getOid()).thenReturn(objectIdentifierValue);
        when(mibValueSymbol.getParent()).thenReturn(mibValueSymbol);
        when(mibValueSymbol.getParent().getOid()).thenReturn(objectIdentifierValue);
        when(mibValueSymbol.getChildren()).thenReturn(new MibValueSymbol[]{});
        when(snmpNotificationType.getDescription()).thenReturn("");

        List<Register> register = handler.convertSnmpObjectToRegister(mibSymbols);

        Assert.assertNotNull(register);
    }
}
