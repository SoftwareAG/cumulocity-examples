/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, 
 * and/or its subsidiaries and/or its affiliates and/or their licensors. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cumulocity.agent.snmp.device;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.snmp4j.TransportMapping;
import org.snmp4j.agent.BaseAgent;
import org.snmp4j.agent.CommandProcessor;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.MOGroup;
import org.snmp4j.agent.ManagedObject;
import org.snmp4j.agent.mo.DefaultMOMutableRow2PC;
import org.snmp4j.agent.mo.DefaultMOTable;
import org.snmp4j.agent.mo.MOAccessImpl;
import org.snmp4j.agent.mo.MOColumn;
import org.snmp4j.agent.mo.MOMutableTableModel;
import org.snmp4j.agent.mo.MOScalar;
import org.snmp4j.agent.mo.MOTableIndex;
import org.snmp4j.agent.mo.MOTableSubIndex;
import org.snmp4j.agent.mo.snmp.RowStatus;
import org.snmp4j.agent.mo.snmp.SnmpCommunityMIB;
import org.snmp4j.agent.mo.snmp.SnmpNotificationMIB;
import org.snmp4j.agent.mo.snmp.SnmpTargetMIB;
import org.snmp4j.agent.mo.snmp.StorageType;
import org.snmp4j.agent.mo.snmp.VacmMIB;
import org.snmp4j.agent.security.MutableVACM;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.Gauge32;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.SMIConstants;
import org.snmp4j.smi.Variable;
import org.snmp4j.transport.TransportMappings;

public abstract class SnmpDevice extends BaseAgent {

    private static final OID sysDescr = new OID("1.3.6.1.2.1.1.1.0");
    private static int incidents;
    private static MOScalar<OctetString> descriptionMO;
    private static MOScalar<Counter32> incidentCountMO;
    private static MOScalar<Counter32> cpuIntegerMO;
    private static SnmpDevice device;
    private String address;
    private String engineId;
    private MOScalar<Variable> customVariableMO = null;

    public SnmpDevice(String address, String engineId) {

        /*
         * These files does not exist and are not used but has to be specified Read
         * snmp4j docs for more info
         */

        /*
         * EngineId 123456 and engineId which is received from Integration tests should
         * match. Else, Simulator won't return value
         */
        super(new File("conf.agent"), new File("bootCounter.agent"), new CommandProcessor(new OctetString(engineId)));
        this.address = address;
        this.engineId = engineId;
    }

    public String getAddress() {
        return address;
    }

    public String getEngineId() {
        return engineId;
    }

    public void setVariable(Variable variable, String oId) {
        this.customVariableMO = new MOScalar<Variable>(new OID(oId), MOAccessImpl.ACCESS_READ_ONLY, variable);
    }

    public void startSnmpSimulator() throws Exception {
        device = this;
        device.init();
        device.addShutdownHook();
        device.getServer().addContext(new OctetString("public"));
        device.finishInit();
        device.run();
        device.sendColdStartNotification();
        createManagedObjects(device);
        System.out.println("SNMP Device Started");

        Random random = new Random();
        while (true) {
            incidents = incidents + 1;
            incidentCountMO.setValue(new Counter32(incidents));
            cpuIntegerMO.setValue(new Counter32(random.nextInt(100)));
            Thread.sleep(100);
            if (incidents == 32000) {
                incidents = 0;
            }
        }
    }

    public static void stopSnmpSimulator() {
        System.out.println("SNMP Device Shutting down!");
        if (device != null && device.getAgentState() == BaseAgent.STATE_RUNNING) {
            device.stop();
        }
    }

    public void createManagedObjects(SnmpDevice agent) throws Exception {
        // unregister default objects created by base agent
        agent.unregisterManagedObject(agent.getSnmpv2MIB());

        agent.registerManagedObject(new MOScalar<OctetString>(sysDescr, MOAccessImpl.ACCESS_READ_ONLY,
                new OctetString("SNMP Device for testing snmp-agent integration")));

        // newrelicExampleIntegerMO
        cpuIntegerMO = new MOScalar<Counter32>(new OID("1.3.6.1.4.1.52032.1.1.1.0"),
                MOAccessImpl.ACCESS_READ_ONLY, new Counter32(107));
        agent.registerManagedObject(cpuIntegerMO);

        // incidentCountMO
        incidentCountMO = new MOScalar<Counter32>(new OID("1.3.6.1.4.1.52032.1.1.2.0"), MOAccessImpl.ACCESS_READ_ONLY,
                new Counter32(3));
        agent.registerManagedObject(incidentCountMO);

        // descriptionMO
        descriptionMO = new MOScalar<OctetString>(new OID("1.3.6.1.4.1.52032.1.1.3.0"), MOAccessImpl.ACCESS_READ_ONLY,
                new OctetString("SNMP Test Agent"));
        agent.registerManagedObject(descriptionMO);

        // ratingMO
        MOScalar<OctetString> ratingMO = new MOScalar<OctetString>(new OID("1.3.6.1.4.1.52032.1.1.4.0"),
                MOAccessImpl.ACCESS_READ_ONLY, new OctetString("1.0.0"));
        agent.registerManagedObject(ratingMO);

        //customMo
        if (this.customVariableMO != null) {
            agent.registerManagedObject(this.customVariableMO);
        }

        // build a table.
        OID tableRootOid = new OID("1.3.6.1.4.1.52032.1.2.1.1");
        MOTableSubIndex[] subIndexes = new MOTableSubIndex[] { new MOTableSubIndex(SMIConstants.SYNTAX_INTEGER) };
        MOTableIndex indexDef = new MOTableIndex(subIndexes, false);
        List<MOColumn<Variable>> columns = new ArrayList<MOColumn<Variable>>();
        // add columns
        columns.add(new MOColumn<Variable>(1, SMIConstants.SYNTAX_OCTET_STRING, MOAccessImpl.ACCESS_READ_ONLY));
        columns.add(new MOColumn<Variable>(2, SMIConstants.SYNTAX_GAUGE32, MOAccessImpl.ACCESS_READ_ONLY));
        columns.add(new MOColumn<Variable>(3, SMIConstants.SYNTAX_GAUGE32, MOAccessImpl.ACCESS_READ_ONLY));
        columns.add(new MOColumn<Variable>(4, SMIConstants.SYNTAX_GAUGE32, MOAccessImpl.ACCESS_READ_ONLY));
        columns.add(new MOColumn<Variable>(5, SMIConstants.SYNTAX_OCTET_STRING, MOAccessImpl.ACCESS_READ_ONLY));
        columns.add(new MOColumn<Variable>(6, SMIConstants.SYNTAX_OCTET_STRING, MOAccessImpl.ACCESS_READ_ONLY));
        DefaultMOTable ifTable = new DefaultMOTable(tableRootOid, indexDef, columns.toArray(new MOColumn[0]));
        MOMutableTableModel<DefaultMOMutableRow2PC> model = (MOMutableTableModel<DefaultMOMutableRow2PC>) ifTable.getModel();

        int rowNumber = 0;
        // add table row
        Variable[] firstRow = new Variable[columns.size()];
        rowNumber = 1;
        firstRow[0] = new OctetString("Test Data 1");
        firstRow[1] = new Gauge32(62);
        firstRow[2] = new Gauge32(24);
        firstRow[3] = new Gauge32(12);
        model.addRow(new DefaultMOMutableRow2PC(new OID(String.valueOf(rowNumber)), firstRow));

        Variable[] secondRow = new Variable[columns.size()];
        rowNumber = 2;
        secondRow[0] = new OctetString("Test Data 2");
        secondRow[1] = new Gauge32(23);
        secondRow[2] = new Gauge32(24);
        secondRow[3] = new Gauge32(6);
        model.addRow(new DefaultMOMutableRow2PC(new OID(String.valueOf(rowNumber)), secondRow));

        Variable[] thirdRow = new Variable[columns.size()];
        rowNumber = 3;
        thirdRow[0] = new OctetString("Test Data 3");
        thirdRow[1] = new Gauge32(54);
        thirdRow[2] = new Gauge32(5);
        thirdRow[3] = new Gauge32(15);
        model.addRow(new DefaultMOMutableRow2PC(new OID(String.valueOf(rowNumber)), thirdRow));
        //
        ifTable.setVolatile(true);
        agent.registerManagedObject(ifTable);
    }

    @Override
    protected void registerManagedObjects() {
    }

    public void registerManagedObject(ManagedObject mo) {
        try {
            server.register(mo, null);
        } catch (DuplicateRegistrationException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void unregisterManagedObject(MOGroup moGroup) {
        moGroup.unregisterMOs(server, getContext(moGroup));
    }

    @Override
    protected void addNotificationTargets(SnmpTargetMIB targetMIB, SnmpNotificationMIB notificationMIB) {
    }

    /**
     * SNMP v1/2/3 access
     */
    @Override
    protected void addViews(VacmMIB vacm) {
        vacm.addGroup(SecurityModel.SECURITY_MODEL_SNMPv1, new OctetString("cpublic"), new OctetString("v1v2group"),
                StorageType.nonVolatile);
        vacm.addGroup(SecurityModel.SECURITY_MODEL_SNMPv2c, new OctetString("cpublic"), new OctetString("v1v2group"),
                StorageType.nonVolatile);
        vacm.addGroup(SecurityModel.SECURITY_MODEL_USM, new OctetString("v3notify"), new OctetString("v3group"),
                StorageType.nonVolatile);
        vacm.addGroup(SecurityModel.SECURITY_MODEL_USM, new OctetString("adminUser"), new OctetString("v3group"),
                StorageType.nonVolatile);
        vacm.addGroup(SecurityModel.SECURITY_MODEL_USM, new OctetString("testUser"), new OctetString("v1v2group"),
                StorageType.nonVolatile);
        vacm.addAccess(new OctetString("v1v2group"), new OctetString("public"), SecurityModel.SECURITY_MODEL_ANY,
                SecurityLevel.NOAUTH_NOPRIV, MutableVACM.VACM_MATCH_EXACT, new OctetString("fullReadView"),
                new OctetString("fullWriteView"), new OctetString("fullNotifyView"), StorageType.nonVolatile);
        vacm.addAccess(new OctetString("v3group"), new OctetString(), SecurityModel.SECURITY_MODEL_USM,
                SecurityLevel.AUTH_NOPRIV, MutableVACM.VACM_MATCH_EXACT, new OctetString("fullReadView"),
                new OctetString("fullWriteView"), new OctetString("fullNotifyView"), StorageType.nonVolatile);
        vacm.addViewTreeFamily(new OctetString("fullReadView"), new OID("1.3"), new OctetString(),
                VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
        vacm.addViewTreeFamily(new OctetString("fullWriteView"), new OID("1.3"), new OctetString(),
                VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
        vacm.addViewTreeFamily(new OctetString("fullNotifyView"), new OID("1.3"), new OctetString(),
                VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
    }

    /**
     * SNMP v3 access
     */
    protected final void addUsmUser(final USM usm) {
        UsmUser user = new UsmUser(new OctetString("v3notify"), null, null, null, null);
        usm.addUser(user.getSecurityName(), null, user);

        user = new UsmUser(new OctetString("adminUser"), AuthMD5.ID, new OctetString("MD5AuthPassword"), PrivDES.ID,
                new OctetString("DESPrivPassword"));
        usm.addUser(user.getSecurityName(), usm.getLocalEngineID(), user);

        user = new UsmUser(new OctetString("testUser"), AuthSHA.ID, new OctetString("testAuthPass"), PrivDES.ID,
                new OctetString("testPrivPass"));
        usm.addUser(user.getSecurityName(), user);
    }

    protected abstract void initTransportMappings();

    protected void unregisterManagedObjects() {
        // here we should unregister those objects previously registered...
    }

    /**
     * The table of community strings configured in the SNMP engine's Local
     * Configuration Datastore (LCD).
     * <p>
     * We only configure one, "public".
     */
    @Override
    protected final void addCommunities(final SnmpCommunityMIB communityMIB) {
        Variable[] com2sec = new Variable[] { new OctetString("public"), // community name
                new OctetString("cpublic"), // security name
                getAgent().getContextEngineID(), // local engine ID
                new OctetString("public"), // default context name
                new OctetString(), // transport tag
                new Integer32(StorageType.nonVolatile), // storage type
                new Integer32(RowStatus.active) // row status
        };
        SnmpCommunityMIB.SnmpCommunityEntryRow row = communityMIB.getSnmpCommunityEntry()
                .createRow(new OctetString("public2public").toSubIndex(true), com2sec);
        communityMIB.getSnmpCommunityEntry().addRow(row);
    }

}
