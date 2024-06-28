---
weight: 10
title: 1 Purpose of the microservice
layout: bundle
---

### Description

Simple Network Management protocol (SNMP) is an application layer protocol, used widely in network management for monitoring network devices.

There are two components that help SNMP-enabled devices to connect to the Cumulocity IoT platform:

- The Mibparser microservice helps in converting a Managed Information Base (MIB) file to a JSON representation which is then used to create a device protocol. The Mibparser microservice is also responsible for enabling SNMP options and the SNMP agent depends on it to show the SNMP tab after registering the agent device.

- The SNMP agent is a device-side agent that helps SNMP-enabled devices to connect to the Cumulocity IoT platform and translates messages from an SNMP-specific format to a Cumulocity IoT model before forwarding them to the Cumulocity IoT platform.

### Type

* Mibparser microservice: Multi-tenant, server-side microservice. Runs on Kubernetes.
  The management service is a multi-tenant microservice and should be uploaded once to the Management tenant and get subscribed by different tenants.

* SNMP agent: Device-side agent, delivered as RPM package, single tenant.
  The SNMP agent is configured by the customers themselves in their environment.

{{< c8y-admon-info >}}
This document mainly describes the Mibparser microservice and not the SNMP agent. The installation steps for the SNMP agent can be found in the [Device management > Protocol integration > SNMP](https://cumulocity.com/docs/protocol-integration/snmp/) in the user documentation.
{{< /c8y-admon-info >}}

### Persistence

The Mibparser microservice does not persist any data locally. All the data is persisted in the Cumulocity IoT Core platform.

### Statefulness

The Mibparser microservice is stateless.

### Scalability

The Mibparser microservice is a multi-tenant microservice shared by all subscribed tenants. It can be scalable if needed. However, the scaling is disabled by default because the default manifest file (*cumulocity.json*) does not contain the 'scale' option.

### Availability of service

The microservice is built every release automatically.  
The microservice is open-source and available for every customer.
