## Cumulocity MIB Parser Java ##


This repository contains [Cumulocity][1] based java microservice. This service will accept zip file, extracts SNMP Trap information based on SMIv2 and converts it to Cumulocity based JSON.

### How do I get set up? ###

* This microservice accepts zip file and REST endpoint will be `{{url}}/service/mibparser/mib/uploadzip`
* Following items are expected inside the zip file
	1. One or more main MIB files along with its dependent MIB files.
	2. A text file named `mib-index.txt` should be added inside zip file which holds name of all main MIB files from which TRAP information should be extracted.
	3. In `mib-index.txt` file, name of each main MIB file should be added in new line. 
	    (Note: Comma seprated values not allowed)
* Build project (Command: `mvn clean install`).

### As uploading Microservice zip file
* Clone/download this project and build.
* After building successfully you can find `Mibparser-<version>.zip` file under `target` folder inside project folder.
* Upload this zip file as it is. While uploading, select subscribe application once uploaded.
* Refer this [link][2] on how to upload microservice into Cumulocity.

### As Jar Execution
* Create `.mibparser` folder in user home directory i.e. `${user.home}/.mibparser`.
* Copy the `mibparer.properties` file from configuration folder to `.mibparser` folder.
* Update details like `C8Y.baseURL` where it should point to and `server.port` on which port this service should run. 
* For e.g `C8Y.baseURL=https://developers.cumulocity.com` and `server.port=6690`
* Execute jar which is in target folder (Command: `java -jar <file-name>.jar`).
* For further steps, go to `Post Jar/RPM Installation`

### As RPM Execution
* Clone/download this project and build.
* You can find rpm in `target/rpm/c8y-mib-parser/RPMS/noarch/` which can be installed.
* For further steps, go to `Post Jar/RPM Installation`

### Post Jar/RPM Installation
* For standalone (jar/rpm) installations, one more step required. That is adding a tenant option for management tenant.
* This can be done by sending a `POST` request on the Cumulocity platform.
  
  POST `{{url}}/tenant/options`
  
  Body
  `{
      "category": "<microservice_name>",
      "key": "microservice.url",
      "value": "<Cumulocity Platform URL:microservice_port>"
  }`
  
  E.g.
  `{
       "category": "mibparser",
       "key": "microservice.url",
       "value": "https://developers.cumulocity.com:6690"
   }`
* This microservice will be listed in the `Own application` under `Administration` tab.
* User should subscribe this misroservice in order to use it. More information on how to subscribe application for the given user using REST interface can be found [here][3].
* Above step can be achieved via user interface as well. `Administration` -> `Tenants` -> `Subtenants` -> `Select user` -> Click on `Application tab` -> Click `subscribe` on mouse hover `Mibparser` from the list of applications.

[1]: http://www.cumulocity.com
[2]: https://cumulocity.com/guides/users-guide/administration/#managing-applications
[3]: https://cumulocity.com/guides/microservice-sdk/rest/