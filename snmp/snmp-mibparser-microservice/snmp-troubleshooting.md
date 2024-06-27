---
weight: 80
title: 8 Diagnosis and troubleshooting
layout: bundle
---

### <a name="health-check">Health/liveness check</a>

#### Via the Administration application

1. Navigate to **Applications** > **Own applications**.
1. Select the "Snmp-mib-parser" application.
1. Open the **Status** tab.

![SNMP Mibparser microservice status](/images/snmp/snmp-mibparser-microservice-status.png)

#### Via health endpoint

The Mibparser microservice exposes the `/health` endpoint.
It shows if the service is up and running or unavailable to handle the request.

**Examples:**

* Executing health call to the Kubernetes pod inside core nodes:

```shell  
curl snmp-mib-parser-scope-management.{{environment-name}}.svc.cluster.local/health
```

* Executing health call externally (tenant must be subscribed to the application):  

```shell
curl {tenant-domain}/service/snmp-mib-parser/health -H 'Authorization: Basic {auth}'
```

Response returned when the agent is running successfully:
```  
HTTP/1.1 200  
{"status":"UP"}
```

Response returned when agent service is unavailable:  
```
HTTP/1.1 503  
{"status":"DOWN"}
```

### Readiness check

See [Health/liveness check](#health-check).