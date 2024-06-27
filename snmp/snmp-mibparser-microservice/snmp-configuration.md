---
weight: 60
title: 6 Configuration
layout: bundle
---

### Configuration files

No configuration file is used. The following are the environment variables provided to the Kubernetes deployment automatically.

```
SERVER_PORT:                        80
C8Y_BOOTSTRAP_REGISTER:             false
MICROSERVICE_SUBSCRIPTION_ENABLED:  true
APPLICATION_NAME:                   snmp-mib-parser
APPLICATION_KEY:                    snmp-mib-parser-key
C8Y_BASEURL:                        http://cumulocity:8111
C8Y_BASEURL_MQTT:                   tcp://cumulocity:1883
C8Y_MICROSERVICE_ISOLATION:         MULTI_TENANT
MEMORY_LIMIT:                       512M
C8Y_BOOTSTRAP_TENANT:               management
C8Y_BOOTSTRAP_USER:                 servicebootstrap_snmp-mib-parser
C8Y_BOOTSTRAP_PASSWORD:             *****
```

### Required configuration of core nodes

There is no specific requirement for core nodes configuration.

### System options

Not applicable.

### Tenant options

Not applicable.

### Other configurations

The Mibparser microservice must be deployed to the Management tenant and must be subscribed to the tenants that intend to use the SNMP feature.
