/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.devicebootstrap;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import c8y.trackeragent.operations.OperationExecutor;
import c8y.trackeragent.operations.OperationDispatcher;
import c8y.trackeragent.service.TrackerDeviceContextService;

@Component
public class TenantBinder {
    
    private static Logger logger = LoggerFactory.getLogger(TenantBinder.class);

    private final DeviceCredentialsRepository credentialsRepository;
    private final TrackerDeviceContextService contextService;
	private final ScheduledExecutorService operationsExecutor;
	private final OperationExecutor operationHelper;
    
    private static final int OPERATIONS_THREAD_POOL_SIZE = 10;

    @Autowired
    public TenantBinder(
    		TrackerDeviceContextService contextService, 
            DeviceCredentialsRepository deviceCredentialsRepository, 
            OperationExecutor operationHelper) {
        this.credentialsRepository = deviceCredentialsRepository;
        this.contextService = contextService;
		this.operationHelper = operationHelper;
		this.operationsExecutor = Executors.newScheduledThreadPool(OPERATIONS_THREAD_POOL_SIZE);
    }
    
    public void init() {
        for (DeviceCredentials deviceCredentials : credentialsRepository.getAllAgentCredentials()) {
            try {
                logger.debug("bind IMEI {}", deviceCredentials.getTenant());
                bind(deviceCredentials.getTenant());
            } catch (Exception e) {
                logger.error("Failed to initialize device: " + deviceCredentials.getImei());
            }
        }
    }
    
    public void bind(final String tenant) {
//    	final DeviceCredentials credentials = credentialsRepository.getAgentCredentials(tenant);
    	logger.info("Bind new tenant " + tenant);
    	startPollerFor(tenant);
    	logger.info("Tenant " + tenant + " bound successfully.");
    }
    
    public void startPollerFor(String tenant) {
        contextService.executeWithContext(
                tenant,
                () -> operationHelper.markOldExecutingOperationsFailed()
        );
//        OperationDispatcher task = new OperationDispatcher(tenantCredentials, contextService, operationHelper);
//        task.startPolling(operationsExecutor);
        logger.info("Started operation polling for tenant {}.", tenant);
    }

}
