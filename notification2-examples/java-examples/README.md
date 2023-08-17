# Notification 2.0 Standalone Java Examples

### Build

* Make sure `notification2-examples/java-examples/src/main/resources/notifications-example.properties` is updated with Cumulocity environment details.
* Run `mvn clean install` at the projects root repository: `notification2-examples`

### Run

* `cd notification2-examples/java-examples/target`
* Run the desired Java class. For example:
`java -cp java-examples-<project.version>-jar-with-dependencies.jar c8y.example.notification.samples.Example1`