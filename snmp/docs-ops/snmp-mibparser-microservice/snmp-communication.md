---
weight: 20
title: 2 Communication
layout: bundle
---

### Data flow diagram

The following illustration grants you a quick overview of the Cumulocity IoT SNMP integration:

![Cumulocity IoT SNMP integration](/images/snmp/snmp-cumulocity-integration.png)


### Open ports

The Mibparser microservice requires the following ports to be open:

|Port number (Default)|TCP/UDP|Name|Description|
|---|---|---|---|
|80|TCP|HTTP REST endpoints| Port is configured by Kubernetes. Accepts REST API calls.|


### External communication

The Mibparser microservice communicates with:

* Core node (using REST API):
  * Register service
  * Get subscribed tenants
  * Create device protocol by parsing Managed Information Base (MIB) files

The SNMP agent communicates with:

* Core node (using REST API and long polling):
  * Handle bootstrap of SNMP device registration
  * Create measurements/events/alarms for the SNMP device
  * Listen for operations and gateway inventory updates (using long polling)

* SNMP-enabled device:
  * Receive SNMP data
