package c8y.trackeragent;

import java.util.concurrent.Callable;

import c8y.trackeragent.exception.SDKExceptions;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.PlatformParameters;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.cumulocity.sdk.client.audit.AuditRecordApi;
import com.cumulocity.sdk.client.cep.CepApi;
import com.cumulocity.sdk.client.devicecontrol.DeviceControlApi;
import com.cumulocity.sdk.client.devicecontrol.DeviceCredentialsApi;
import com.cumulocity.sdk.client.event.EventApi;
import com.cumulocity.sdk.client.identity.IdentityApi;
import com.cumulocity.sdk.client.inventory.BinariesApi;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class TrackerPlatform implements Platform {

    private final Platform orig;
    private ManagedObjectRepresentation agent;
    private Cache<Class<?>, Object> cache;

    public TrackerPlatform(PlatformImpl orig) {
        this.orig = orig;
        this.cache = CacheBuilder.newBuilder().build();
    }

    public InventoryApi getInventoryApi() throws SDKException {
        return new CachedApiGetter<InventoryApi>(InventoryApi.class) {

            @Override
            public InventoryApi call() throws Exception {
                return orig.getInventoryApi();
            }

        }.get();
    }

    public IdentityApi getIdentityApi() throws SDKException {
        return new CachedApiGetter<IdentityApi>(IdentityApi.class) {

            @Override
            public IdentityApi call() throws Exception {
                return orig.getIdentityApi();
            }

        }.get();
    }

    public MeasurementApi getMeasurementApi() throws SDKException {
        return new CachedApiGetter<MeasurementApi>(MeasurementApi.class) {

            @Override
            public MeasurementApi call() throws Exception {
                return orig.getMeasurementApi();
            }

        }.get();
    }

    public DeviceControlApi getDeviceControlApi() throws SDKException {
        return new CachedApiGetter<DeviceControlApi>(DeviceControlApi.class) {

            @Override
            public DeviceControlApi call() throws Exception {
                return orig.getDeviceControlApi();
            }

        }.get();
    }

    public AlarmApi getAlarmApi() throws SDKException {
        return new CachedApiGetter<AlarmApi>(AlarmApi.class) {

            @Override
            public AlarmApi call() throws Exception {
                return orig.getAlarmApi();
            }

        }.get();
    }

    public EventApi getEventApi() throws SDKException {
        return new CachedApiGetter<EventApi>(EventApi.class) {

            @Override
            public EventApi call() throws Exception {
                return orig.getEventApi();
            }

        }.get();
    }

    public AuditRecordApi getAuditRecordApi() throws SDKException {
        return new CachedApiGetter<AuditRecordApi>(AuditRecordApi.class) {

            @Override
            public AuditRecordApi call() throws Exception {
                return orig.getAuditRecordApi();
            }

        }.get();
    }

    public CepApi getCepApi() throws SDKException {
        return new CachedApiGetter<CepApi>(CepApi.class) {

            @Override
            public CepApi call() throws Exception {
                return orig.getCepApi();
            }

        }.get();
    }
    
    @Override
    public BinariesApi getBinariesApi() throws SDKException {
        return new CachedApiGetter<BinariesApi>(BinariesApi.class) {

            @Override
            public BinariesApi call() throws Exception {
                return orig.getBinariesApi();
            }

        }.get();
    }

    public DeviceCredentialsApi getDeviceCredentialsApi() throws SDKException {
        return new CachedApiGetter<DeviceCredentialsApi>(DeviceCredentialsApi.class) {

            @Override
            public DeviceCredentialsApi call() throws Exception {
                return orig.getDeviceCredentialsApi();
            }

        }.get();
    }

    public String getTenantId() {
        return getPlatformParameters().getTenantId();
    }

    public String getHost() {
        return getPlatformParameters().getHost();
    }

    public String getUser() {
        return getPlatformParameters().getUser();
    }

    public String getPassword() {
        return getPlatformParameters().getPassword();
    }

    public PlatformParameters getPlatformParameters() {
        return (PlatformParameters) orig;
    }

    public void setAgent(ManagedObjectRepresentation agentMo) {
        this.agent = agentMo;
    }

    public ManagedObjectRepresentation getAgent() {
        return agent;
    }

    public GId getAgentId() {
        return agent == null ? null : agent.getId();
    }

    @Override
    public String toString() {
        return String.format("TrackerPlatform [orig=%s, getTenantId()=%s, getHost()=%s, getUser()=%s, agentId = %s]", orig, getTenantId(), getHost(), getUser(), getAgentId());
    }

    abstract class CachedApiGetter<V> implements Callable<V> {

        private final Class<V> cacheKey;

        CachedApiGetter(Class<V> cacheKey) {
            this.cacheKey = cacheKey;
        }

        @SuppressWarnings("unchecked")
        V get() throws SDKException {
            try {
                return (V) cache.get(cacheKey, this);
            } catch (Exception e) {
                throw SDKExceptions.narrow(e, "Cant create api " + cacheKey.getSimpleName());
            }
        }
    }

}
