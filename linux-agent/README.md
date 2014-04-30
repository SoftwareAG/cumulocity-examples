This repository contains an example agent for Linux, in particular for the Raspberry Pi. It consists of the following components: 

* lx-driver: Interface classes for writing hardware drivers and implementing new functionality.
* lx-agent: The main executable agent including basic device management, should work on all Java platforms.
* rpi-driver: Hardware driver for the Raspberry Pi.
* piface-support: A simple Piface integration.
* tinkerforge-support: Support for Tinkerforge bricks.

For running the agent,

* Include the required jars into the classpath and run c8y.lx.agent.Agent. 

Maven can help you with collecting the required jars, for example through

	mvn clean install
	cd assembly
	mvn -P rpi-driver,tinkerforge-driver dependency:copy-dependencies
	mvn -P rpi-driver,tinkerforge-driver assembly:single
