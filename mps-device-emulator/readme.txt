To run the emulator:

java -jar mps-device-emulator-0.0.1-SNAPSHOT-jar-with-dependencies.jar

By default the server will start at port 50000. To change it:

java -Dserver.port=8091 -jar mps-device-emulator-0.0.1-SNAPSHOT-jar-with-dependencies.jar

To stop the emulator simply press Ctrl+C from the console or sent SIGINT in any other way to the process.

To add new REST resources just place them inside 'com.cumulocity.agents.mps.emulator' package and mark @JAXRSResource
and they will be automatically scanned and used.
