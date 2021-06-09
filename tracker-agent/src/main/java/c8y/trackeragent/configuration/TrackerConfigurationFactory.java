/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.configuration;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class TrackerConfigurationFactory implements FactoryBean<TrackerConfiguration> {

    @Override
    public Class<?> getObjectType() {
        return TrackerConfiguration.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public TrackerConfiguration getObject() throws Exception {
        return ConfigUtils.get().loadCommonConfiguration();
    }

}
