package com.cumulocity.agent.snmp.bootstrap.service;

import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BootstrapServiceTest {
//
//	@Mock
//	private DataStore dbStore;
//
//	@Mock
//	private IdentityApi identityApi;
//
//	@Mock
//	private TaskScheduler scheduler;
//
//	@Mock
//	private InventoryApi inventoryApi;
//
//	@Mock
//	private Platform bootstrapPlatform;
//
//	@Mock
//	private GatewayProperties properties;
//
//	@InjectMocks
//	private BootstrapService bootstrapService;
//
//	@Mock
//	private PlatformProvider platformProvider;
//
//	@Mock
//	private ApplicationEventPublisher eventPublisher;
//
//	@Mock
//	private DeviceCredentialsApi deviceCredentialsApi;
//
//	@Before
//	public void setup() {
//		GatewayProperties.BootstrapProperties bootstrapConfiguration = properties.new BootstrapProperties();
//		bootstrapConfiguration.setBootstrapDelay(10000L);
//
//		when(properties.getBootstrapProperties()).thenReturn(bootstrapConfiguration);
//		when(bootstrapPlatform.getDeviceCredentialsApi()).thenReturn(deviceCredentialsApi);
//	}
//
//	@Test
//	public void shouldScheduleFixedDelayTask() {
//		bootstrapService.afterPropertiesSet();
//
//		verify(scheduler).scheduleWithFixedDelay(any(Runnable.class), eq(10000L));
//	}
//
//	@Test
//	public void shouldNotTryToPollDeviceCredentialsIfPlatformIsAlreadyAvailable() {
//		ArgumentCaptor<Runnable> pollingDeviceCredentialsCaptor = ArgumentCaptor.forClass(Runnable.class);
//		when(platformProvider.isCredentialsAvailable()).thenReturn(true);
//
//		bootstrapService.afterPropertiesSet();
//
//		verify(scheduler).scheduleWithFixedDelay(pollingDeviceCredentialsCaptor.capture(), eq(10000L));
//		pollingDeviceCredentialsCaptor.getValue().run();
//		verifyZeroInteractions(dbStore);
//		verifyZeroInteractions(eventPublisher);
//		verifyZeroInteractions(identityApi);
//		verifyZeroInteractions(inventoryApi);
//	}
//
//	@Test
//	public void shouldPollCredentialsFromServerIfNotAvailableLocally() {
//		ArgumentCaptor<Runnable> pollingDeviceCredentialsCaptor = ArgumentCaptor.forClass(Runnable.class);
//		DeviceCredentialsRepresentation deviceCredentials = new DeviceCredentialsRepresentation();
//		deviceCredentials.setTenantId("test");
//		deviceCredentials.setUsername("user");
//		deviceCredentials.setPassword("password");
//
//		when(platformProvider.isCredentialsAvailable()).thenReturn(false);
//		when(dbStore.get()).thenReturn(empty());
//		when(platformProvider.getBootstrapPlatform()).thenReturn(bootstrapPlatform);
//		when(deviceCredentialsApi.pollCredentials("gateway#1")).thenReturn(deviceCredentials);
//		when(properties.getGatewayIdentifier()).thenReturn("gateway#1");
//
//		bootstrapService.afterPropertiesSet();
//
//		verify(scheduler).scheduleWithFixedDelay(pollingDeviceCredentialsCaptor.capture(), eq(10000L));
//		pollingDeviceCredentialsCaptor.getValue().run();
//		verify(platformProvider).getBootstrapPlatform();
//		verify(dbStore).store(argThat(new ArgumentMatcher<Serializable>() {
//			@Override
//			public boolean matches(Serializable object) {
//				DeviceCredentials deviceCredentials = (DeviceCredentials) object;
//				return "test".equals(deviceCredentials.getTenantId())
//						&& "user".equals(deviceCredentials.getUsername())
//						&& "password".equals(deviceCredentials.getPassword());
//			}
//		}));
//		verify(eventPublisher).publishEvent(any(CredentialsAvailableEvent.class));
//	}
//
//	@Test
//	public void shouldNotPollCredentialsFromServerIfAvailableLocally() {
//		ArgumentCaptor<Runnable> pollingDeviceCredentialsCaptor = ArgumentCaptor.forClass(Runnable.class);
//		DeviceCredentials deviceCredentials = new DeviceCredentials("test", "user", "password");
//
//		when(platformProvider.isCredentialsAvailable()).thenReturn(false);
//		when(properties.isForcedBootstrap()).thenReturn(false);
//		when(dbStore.get()).thenReturn(Optional.of(deviceCredentials));
//
//		bootstrapService.afterPropertiesSet();
//
//		verify(scheduler).scheduleWithFixedDelay(pollingDeviceCredentialsCaptor.capture(), eq(10000L));
//		pollingDeviceCredentialsCaptor.getValue().run();
//		verify(platformProvider).isCredentialsAvailable();
//		verify(dbStore).get();
//		verifyNoMoreInteractions(dbStore);
//		verifyNoMoreInteractions(platformProvider);
//
//		verify(eventPublisher).publishEvent(any(CredentialsAvailableEvent.class));
//	}
//
//	@Test
//	public void shouldPollCredentialsIfBootstrapIsForced() {
//		ArgumentCaptor<Runnable> pollingDeviceCredentialsCaptor = ArgumentCaptor.forClass(Runnable.class);
//		DeviceCredentialsRepresentation deviceCredentials = new DeviceCredentialsRepresentation();
//		deviceCredentials.setTenantId("test");
//		deviceCredentials.setUsername("user");
//		deviceCredentials.setPassword("password");
//
//		DeviceCredentials localDeviceCredentials = new DeviceCredentials("test", "user", "password");
//
//		when(platformProvider.isCredentialsAvailable()).thenReturn(false);
//		when(properties.isForcedBootstrap()).thenReturn(true);
//		when(dbStore.get()).thenReturn(of(localDeviceCredentials));
//		when(platformProvider.getBootstrapPlatform()).thenReturn(bootstrapPlatform);
//		when(deviceCredentialsApi.pollCredentials("gateway#1")).thenReturn(deviceCredentials);
//		when(properties.getGatewayIdentifier()).thenReturn("gateway#1");
//
//		bootstrapService.afterPropertiesSet();
//
//		verify(scheduler).scheduleWithFixedDelay(pollingDeviceCredentialsCaptor.capture(), eq(10000L));
//		pollingDeviceCredentialsCaptor.getValue().run();
//		verify(platformProvider).getBootstrapPlatform();
//		verify(dbStore).store(argThat(new ArgumentMatcher<Serializable>() {
//			@Override
//			public boolean matches(Serializable object) {
//				DeviceCredentials deviceCredentials = (DeviceCredentials) object;
//				return "test".equals(deviceCredentials.getTenantId())
//						&& "user".equals(deviceCredentials.getUsername())
//						&& "password".equals(deviceCredentials.getPassword());
//			}
//		}));
//		verify(eventPublisher).publishEvent(any(CredentialsAvailableEvent.class));
//	}
}