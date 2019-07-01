## Cumulocity examples

Microservices applications are contained here and they are documented on the online documentation (refer to [Examples](https://cumulocity.com/guides/microservice-sdk/http/) in the Microservice SDK guide).

The current examples of microservice applications are:

#### sample-python-microservice

Python microservice application which uses the Cumulocity REST API and exposes endpoints to:

- verify if the microservice is up and running,
- create a device and random measurements for it,
- get the current application subscriptions for a particular tenant.

#### node-microservice

Node.js-based microservice which exposes endpoints to verify if the microservice is up and running and get some of the environment variables.

It uses the Cumulocity [@c8y/client JavaScript library](https://www.npmjs.com/package/@c8y/client) to subscribe to alarms. When a new alarm is created, a Slack channel gets notified.

#### iptracker-microservice

This microservice application uses our Java SDK to verify user roles and create a warning alarm message (for demonstration purposes). It also exposes endpoints to:

- verify if the microservice is up and running,
- pass a parameter and return a formatted string,
- get some of the environment variables,
- track a user's approximate location (based on IP) and store it in the platform,
- get the tracked IPs and locations.

It also uses the Cumulocity UI to display the tracked locations on a map.
