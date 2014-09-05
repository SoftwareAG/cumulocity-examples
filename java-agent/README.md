This repository contains an example Java Agent with support for Raspberry Pi, Linux, Windows and Mac systems. 
It consists of the following modules: 
 * jv-driver: Interface classes for writing hardware drivers and implementing new functionality.
 * jv-agent: The main executable agent including basic device management, should work on all Java platforms.
 * rpi-driver: Hardware driver for the Raspberry Pi.
 * kontron-driver: Hardware driver for 
 * mac-driver: Hardware driver for Mac OS X systems.
 * generic-linux-driver: Hardware driver for linux systems lacking /proc/cpuinfo. It uses the MAC address to register the device.
 * win-driver: Hardware driver for Windows systems.
 * piface-driver: A simple Piface integration.
 * tinkerforge-driver: Support for Tinkerforge bricks. 
 * assembly: Base packaging for all environments.
 * packages: Environment specific packaging.