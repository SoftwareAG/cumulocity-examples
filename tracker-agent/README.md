#Tracker Agent

## Running agent

1. Compile: _mvn clean install_
2. Configure properties: _/etc/tracker-agent/tracker-agent-server.properties_
3. Start application using _Main.java_

## Example processing device (test)
1. GUI: Device Management -> Devices -> Registration: Create the bootstrap device with id: *860599001073709*
2. Connect by telnet: telnet localhost (*localPort1*)
3. Connect by telnet: 
```
+RESP:GTNMR,300400,860599001073709,,0,1,1,1,0.0,28,43.3,24.950411,60.193572,20161005072235,0244,0091,0D9F,ABEE,,96,20161005072236,2CC0$
```
4. GUI: Device Management -> Devices -> Registration: click Accept

## Migration process

### Process:

The migration process does not expect any extra user interaction. 
On application start, tracker-agent read device.properties file and migrate every relation from the properties file to manage objects.
If the relation between tenant and device already exists, the device is skipped.

### Tenant reassign to another tenant:

To reassign a device to another tenant, first device should be removed from the existing tenant:
1. Find MO by the fragment: _c8y_tenantId_ and field _c8y_tenantId == {currently_assigned_tenant}_.
2. In MO find the device in externalIds list.
3. Remove the device id from MO.
On device call it should be assigned to the new tenant.

#####Example:
Existing Managed Object:
```
{
	"_id" : "10175",
	"childAssets" : [ ],
	"creationTime" : {
		"date" : ISODate("2021-10-22T07:34:27.296Z"),
		"offset" : 0
	},
	"lastUpdated" : {
		"date" : ISODate("2021-10-22T07:34:27.296Z"),
		"offset" : 0
	},
	"owner" : "service_tracker-agent",
	"childDevices" : [ ],
	"childAdditions" : [ ],
	"c8y_tenantId" : "tenant_1",
	"_fragments" : [
		"c8y_tenantId"
	],
	"externalIds" : [
		{
			"type" : "c8y_device_tenant_Imei",
			"value" : "TTV7zu"
		},
		{
			"type" : "c8y_device_tenant_Imei",
			"value" : "TTI35x"
		}
	]
}
```
We want to reassign the device "TTI35x" to another tenant. It is needed to update MO to:
```
{
	"_id" : "10175",
	"childAssets" : [ ],
	"creationTime" : {
		"date" : ISODate("2021-10-22T07:34:27.296Z"),
		"offset" : 0
	},
	"lastUpdated" : {
		"date" : ISODate("2021-10-22T07:34:27.296Z"),
		"offset" : 0
	},
	"owner" : "service_tracker-agent",
	"childDevices" : [ ],
	"childAdditions" : [ ],
	"c8y_tenantId" : "tenant_1",
	"_fragments" : [
		"c8y_tenantId"
	],
	"externalIds" : [
		{
			"type" : "c8y_device_tenant_Imei",
			"value" : "TTV7zu"
		}
	]
}
```

## Troubleshooting

###Port already in use

Tracker-agent required 3 ports to work correctly
Standard SpringBoot port (default is 8689) and two extra ports defined as properties: localPort1 and localPort2
All ports need to not be available locally.
