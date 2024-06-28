---
weight: 40
title: 4 Installation
layout: bundle
---

### <a name="install-instruction">Installation instructions</a>

The SNMP Mibparser microservice ZIP package can be found [here](http://resources.cumulocity.com/examples/snmp/).

There are two ways to install the microservice:

* By uploading the microservice ZIP package via UI to the Management tenant.
See [Platform administration > Standard tenant administration > Managing the ecosystem > Managing applications](https://cumulocity.com/docs/standard-tenant/ecosystem/#managing-applications) for details.

* By locating the microservice ZIP package in the */webapps/2Images* folder on the core machine(s) from which it is automatically taken by Karaf which pushes the microservice to the Kubernetes registry.  
When Karaf has generated the \<microservice_name>.zip.installed content, the push is successful. This can be verified in the UI by checking if the version has changed, or on kube master CLI by executing `kc describe po <pod>` and checking the version in the output.

### Configuration instructions

The Mibparser microservice works without additional configuration.
