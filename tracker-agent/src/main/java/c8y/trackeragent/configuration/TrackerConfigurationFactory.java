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
