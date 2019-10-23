package com.cumulocity.agent.snmp.platform.service;

import com.cumulocity.agent.snmp.bootstrap.model.CredentialsAvailableEvent;
import com.cumulocity.agent.snmp.config.GatewayProperties;
import com.cumulocity.agent.snmp.platform.model.PlatformConnectionReadyEvent;
import com.cumulocity.agent.snmp.platform.pubsub.subscriber.MeasurementSubscriber;
import com.cumulocity.rest.representation.devicebootstrap.DeviceCredentialsRepresentation;
import com.cumulocity.sdk.client.HttpClientConfig;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformParameters;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlatformProviderTest {

    @Mock
    private GatewayProperties gatewayProperties;

    @Mock
    private GatewayProperties.SnmpProperties snmpProperties;

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private Platform bootstrapPlatform;

    @Mock
    private GatewayProperties.BootstrapProperties bootstrapProperties;

    @Mock
    private Platform platform;

    @Mock
    private MeasurementApi measurementApi;

    @InjectMocks
    private PlatformProvider platformProvider;

    @Captor
    private ArgumentCaptor<PlatformConnectionReadyEvent> platformConnectionReadyEventCaptor;

    @Captor
    private ArgumentCaptor<Runnable> platformAvailabilityMonitorRunnable;


    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenPlatformIsNull() {
        platformProvider.getPlatform();
    }

    @Test
    public void shouldCreateBootstrapPlatform() {
        when(gatewayProperties.getBootstrapProperties()).thenReturn(bootstrapProperties);
        when(gatewayProperties.getBaseUrl()).thenReturn("http://baseurl.cumulocity.com");
        when(gatewayProperties.isForceInitialHost()).thenReturn(Boolean.TRUE);

        when(bootstrapProperties.getTenantId()).thenReturn("TENANT_ID");
        when(bootstrapProperties.getUsername()).thenReturn("USERNAME");
        when(bootstrapProperties.getPassword()).thenReturn("PASSWORD");

        platformProvider.afterPropertiesSet();

        verify(gatewayProperties).getBaseUrl();
        verify(gatewayProperties).isForceInitialHost();
        verify(bootstrapProperties).getTenantId();
        verify(bootstrapProperties).getUsername();
        verify(bootstrapProperties).getPassword();

        assertNotNull(platformProvider.getBootstrapPlatform());

        PlatformParameters platformParameters = (PlatformParameters) platformProvider.getBootstrapPlatform();
        assertEquals("http://baseurl.cumulocity.com/", platformParameters.getHost());
        assertEquals("TENANT_ID", platformParameters.getTenantId());
        assertEquals("USERNAME", platformParameters.getUser());
    }

    @Test
    public void shouldCreateAndConfigurePlatformOnCredentialsAvailableEvent() {
        DeviceCredentialsRepresentation credentials = new DeviceCredentialsRepresentation();
        credentials.setTenantId("TENANT_ID");
        credentials.setUsername("USERNAME");
        credentials.setPassword("PASSWORD");

        when(gatewayProperties.getBaseUrl()).thenReturn("http://baseurl.cumulocity.com");
        when(gatewayProperties.isForceInitialHost()).thenReturn(Boolean.TRUE);
        when(gatewayProperties.getThreadPoolSizeForScheduledTasks()).thenReturn((25 * 80 / 100));
        when(gatewayProperties.getBootstrapFixedDelay()).thenReturn(Integer.valueOf(1001));

        platformProvider.onCredentialsAvailable(new CredentialsAvailableEvent(credentials));

        verify(gatewayProperties).getBaseUrl();
        verify(gatewayProperties).isForceInitialHost();
        verify(gatewayProperties).getThreadPoolSizeForScheduledTasks();

        assertNotNull(platformProvider.getPlatform());

        PlatformParameters platformParameters = (PlatformParameters) platformProvider.getPlatform();
        assertEquals("http://baseurl.cumulocity.com/", platformParameters.getHost());
        assertEquals("TENANT_ID", platformParameters.getTenantId());
        assertEquals("USERNAME", platformParameters.getUser());

        MeasurementSubscriber.MeasurementRepresentation resource = new MeasurementSubscriber.MeasurementRepresentation();
        String jsonString = "{\"JSON\":\"VALUE\"}";
        ReflectionTestUtils.setField(resource, "jsonString", jsonString);
        assertEquals(platformParameters.getResponseMapper().write(resource), jsonString);

        Object someOtherObject = new Object();
        assertNull(platformParameters.getResponseMapper().write(someOtherObject));

        assertNull(platformParameters.getResponseMapper().read(null, null));

        HttpClientConfig httpClientConfig = platformParameters.getHttpClientConfig();
        assertEquals(20, httpClientConfig.getPool().getMax());
        assertEquals(20, httpClientConfig.getPool().getPerHost());

        verify(eventPublisher).publishEvent(platformConnectionReadyEventCaptor.capture());
        assertEquals("USERNAME", platformConnectionReadyEventCaptor.getValue().getCurrentUser());

        verify(taskScheduler).scheduleWithFixedDelay(platformAvailabilityMonitorRunnable.capture(), eq(Duration.ofMillis(1001)));
    }

    @Test
    public void shouldCheckPlatformAvailabilityMonitorLogic_whenPlatformIsAvailable() {
        when(gatewayProperties.getBootstrapFixedDelay()).thenReturn(Integer.valueOf(1111));

        // Inject Mock Platform
        ReflectionTestUtils.setField(platformProvider, "platform", platform);

        // Invoke startPlatformAvailabilityMonitor and capture the Runnable
        ReflectionTestUtils.invokeMethod(platformProvider, "startPlatformAvailabilityMonitor");

        verify(taskScheduler).scheduleWithFixedDelay(platformAvailabilityMonitorRunnable.capture(), eq(Duration.ofMillis(1111)));
        verify(gatewayProperties).getBootstrapFixedDelay();

        // Verify runnable logic when isPlatformAvailable = true
        ReflectionTestUtils.setField(platformProvider, "isPlatformAvailable", Boolean.TRUE);

        platformAvailabilityMonitorRunnable.getValue().run();

        verifyZeroInteractions(platform);
    }

    @Test
    public void shouldCheckPlatformAvailabilityMonitorLogic_whenPlatformBecomesAvailable() {
        when(gatewayProperties.getBootstrapFixedDelay()).thenReturn(Integer.valueOf(1111));

        // Inject Mock Platform
        ReflectionTestUtils.setField(platformProvider, "platform", platform);

        // Invoke startPlatformAvailabilityMonitor and capture the Runnable
        ReflectionTestUtils.invokeMethod(platformProvider, "startPlatformAvailabilityMonitor");

        verify(taskScheduler).scheduleWithFixedDelay(platformAvailabilityMonitorRunnable.capture(), eq(Duration.ofMillis(1111)));
        verify(gatewayProperties).getBootstrapFixedDelay();

        // Reset the gatewayProperties
        reset(gatewayProperties);

        // Verify runnable logic when isPlatformAvailable = false & PlatformBecomesAvailable
        ReflectionTestUtils.setField(platformProvider, "isPlatformAvailable", Boolean.FALSE);
        when(platform.getMeasurementApi()).thenReturn(measurementApi);

        platformAvailabilityMonitorRunnable.getValue().run();

        assertTrue(platformProvider.isPlatformAvailable());

        verify(platform).getMeasurementApi();
        verify(gatewayProperties, times(0)).getBootstrapFixedDelay();
    }

    @Test
    public void shouldCheckPlatformAvailabilityMonitorLogic_whenPlatformBecomesUnavailable() {
        when(gatewayProperties.getBootstrapFixedDelay()).thenReturn(Integer.valueOf(1111));

        // Inject Mock Platform
        ReflectionTestUtils.setField(platformProvider, "platform", platform);

        // Invoke startPlatformAvailabilityMonitor and capture the Runnable
        ReflectionTestUtils.invokeMethod(platformProvider, "startPlatformAvailabilityMonitor");

        verify(taskScheduler).scheduleWithFixedDelay(platformAvailabilityMonitorRunnable.capture(), eq(Duration.ofMillis(1111)));
        verify(gatewayProperties).getBootstrapFixedDelay();

        // Reset the gatewayProperties
        reset(gatewayProperties);

        // Verify runnable logic when isPlatformAvailable = false & PlatformBecomesUnavailable
        ReflectionTestUtils.setField(platformProvider, "isPlatformAvailable", Boolean.FALSE);
        when(platform.getMeasurementApi()).thenReturn(null);

        platformAvailabilityMonitorRunnable.getValue().run();

        assertFalse(platformProvider.isPlatformAvailable());

        verify(platform).getMeasurementApi();
        verify(gatewayProperties, times(1)).getBootstrapFixedDelay();
    }

    @Test
    public void shouldCheckPlatformAvailabilityMonitorLogic_whenPlatformCheckThrowsException() {
        when(gatewayProperties.getBootstrapFixedDelay()).thenReturn(Integer.valueOf(1111));

        // Inject Mock Platform
        ReflectionTestUtils.setField(platformProvider, "platform", platform);

        // Invoke startPlatformAvailabilityMonitor and capture the Runnable
        ReflectionTestUtils.invokeMethod(platformProvider, "startPlatformAvailabilityMonitor");

        verify(taskScheduler).scheduleWithFixedDelay(platformAvailabilityMonitorRunnable.capture(), eq(Duration.ofMillis(1111)));
        verify(gatewayProperties).getBootstrapFixedDelay();

        // Reset the gatewayProperties
        reset(gatewayProperties);

        // Verify runnable logic when isPlatformAvailable = false & PlatformBecomesUnavailable
        ReflectionTestUtils.setField(platformProvider, "isPlatformAvailable", Boolean.FALSE);
        when(platform.getMeasurementApi()).thenThrow(new NullPointerException());

        platformAvailabilityMonitorRunnable.getValue().run();

        assertFalse(platformProvider.isPlatformAvailable());

        verify(platform).getMeasurementApi();
        verify(gatewayProperties, times(1)).getBootstrapFixedDelay();
    }

    @Test
    public void shouldMarkPlatformunAvailable() {
        assertFalse(platformProvider.isPlatformAvailable());

        // Set Platform as available
        ReflectionTestUtils.setField(platformProvider, "isPlatformAvailable", Boolean.TRUE);
        assertTrue(platformProvider.isPlatformAvailable());

        platformProvider.markPlatfromAsUnavailable();

        assertFalse(platformProvider.isPlatformAvailable());
    }

    @Test
    public void shouldNotCreatePlatformOnCredentialsAvailableEventWhenPlatformIsNotNull() {
        ReflectionTestUtils.setField(platformProvider, "platform", platform);

        platformProvider.onCredentialsAvailable(new CredentialsAvailableEvent(new DeviceCredentialsRepresentation()));

        assertEquals(platform, platformProvider.getPlatform());
    }
}