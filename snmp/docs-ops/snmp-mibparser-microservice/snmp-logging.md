---
weight: 70
title: 7 Logging
layout: bundle
---

### Log files

The Mibparser microservice uses the logback framework for logging. The configuration is located at */etc/snmp-mib-parser/snmp-mib-parser-logging.xml* inside the Kubernetes pod and logs at INFO level by default.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true" scanPeriod="180 seconds">
	<include resource="org/springframework/boot/logging/logback/defaults.xml" />
	<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
		<encoder>
			<pattern>${FILE_LOG_PATTERN}</pattern>
		</encoder>
	</appender>

	<logger name="com.cumulocity" level="INFO" />
	<logger name="c8y" level="INFO" />
	<logger name="httpclient.wire" level="INFO" />

	<root level="INFO">
		<appender-ref ref="STDOUT" />
	</root>
</configuration>
```

The microservice logs to standard output. Logs can be collected on kube master with:   

	kubectl -n <namespace> logs <pod>
