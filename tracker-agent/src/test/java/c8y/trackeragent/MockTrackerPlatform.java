package c8y.trackeragent;

import static com.cumulocity.model.idtype.GId.asGId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import c8y.trackeragent.TrackerPlatform;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.audit.AuditRecordApi;
import com.cumulocity.sdk.client.cep.CepApi;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.measurement.MeasurementApi;

public class MockTrackerPlatform extends TrackerPlatform {
    
    private String tenantId = "sampleTenant";
    private String host = "localhost";
    private String user = "user";
    private String password = "password";
    
    private InventoryApi inventoryApi = mock(InventoryApi.class);
    private IdentityApi identityApi = mock(IdentityApi.class);
    private MeasurementApi measurementApi = mock(MeasurementApi.class);
    private DeviceControlApi deviceControlApi = mock(DeviceControlApi.class);
    private AlarmApi alarmApi =  mock(AlarmApi.class);
    private EventApi eventApi = mock(EventApi.class);
    private AuditRecordApi auditRecordApi = mock(AuditRecordApi.class);
    private CepApi cepApi = mock(CepApi.class);
    private DeviceCredentialsApi deviceCredentialsApi = mock(DeviceCredentialsApi.class);
    private ManagedObjectRepresentation agent = mock(ManagedObjectRepresentation.class);

    
    public MockTrackerPlatform(String tenantId) {
        super(null);
        this.tenantId = tenantId;
        when(agent.getId()).thenReturn(asGId(tenantId + "Agent"));
        when(agent.getSelf()).thenReturn("http://me");
    }

    public MockTrackerPlatform() {
        this("sampleTenant");
    }

    @Override
    public InventoryApi getInventoryApi() throws SDKException {
        return inventoryApi;
    }

    @Override
    public IdentityApi getIdentityApi() throws SDKException {
        return identityApi;
    }

    @Override
    public MeasurementApi getMeasurementApi() throws SDKException {
        return measurementApi;
    }

    @Override
    public DeviceControlApi getDeviceControlApi() throws SDKException {
        return deviceControlApi;
    }

    @Override
    public AlarmApi getAlarmApi() throws SDKException {
        return alarmApi;
    }

    @Override
    public EventApi getEventApi() throws SDKException {
        return eventApi;
    }

    @Override
    public AuditRecordApi getAuditRecordApi() throws SDKException {
        return auditRecordApi;
    }

    @Override
    public CepApi getCepApi() throws SDKException {
        return cepApi;
    }

    @Override
    public DeviceCredentialsApi getDeviceCredentialsApi() throws SDKException {
        return deviceCredentialsApi;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String getUser() {
        return user ;
    }

    @Override
    public String getPassword() {
        return password ;
    }

    @Override
    public ManagedObjectRepresentation getAgent() {
        return agent;
    }

}
