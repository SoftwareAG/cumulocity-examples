package com.cumulocity.greenbox.server;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "com.cumulocity.greenbox.server", excludeFilters = @Filter(type = FilterType.ANNOTATION, value = Configuration.class))
public class GreenBoxAgentFeature {

}
