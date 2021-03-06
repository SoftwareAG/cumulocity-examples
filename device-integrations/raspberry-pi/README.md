# Raspberry Pi

-----------------------------------

## Overview

The [Raspberry Pi](http://en.wikipedia.org/wiki/Raspberry_Pi) is a popular, low-cost mini computer running Linux. It is ideally suited for prototyping machine-to-machine solutions through its GPIO pins and USB support.

In this section, we describe how to install a Cumulocity agent with all relevant drivers on the Raspberry Pi to be able to remotely manage the Raspberry Pi and its connected sensors and controls. This allows you to

* Use basic device management functionality.
* Identify individual Raspberry Pis remotely based on their hardware serial number.
* Update the Pi's firmware remotely through the firmware repository on GitHub.
* Use the [PiFace Digital](http://www.element14.com/community/docs/DOC-52857/l/piface-digital-for-raspberry-pi) adapter board from the cloud.
* Use [TinkerForge](/guides/images/devices/tinkerforge) sensors and controls from the cloud.

> The agent is provided in open source form as-is without support or warranty. For commercial use, we recommend to use industrial hardware and/or the Cumulocity C++ SDK.

![Raspberry Pi image](images/640px-RaspberryPi.jpg)

### Prerequisites

To install the agent, you need a Raspberry Pi with an installation of Java SE, Version 7 or later. Java SE is pre-installed in recent distributions of [Raspbian](http://www.raspberrypi.org/downloads), the default Linux distribution of the Raspberry Pi. To verify, simply type

```shell
$ java -version
```

You also need to know the serial number of your Raspberry Pi to register it with Cumulocity:

```shell
$ cat /proc/cpuinfo
Processor	: ARMv6-compatible processor rev 7 (v6l)
BogoMIPS	: 464.48
Features	: swp half thumb fastmult vfp edsp java tls
CPU implementer	: 0x41
CPU architecture: 7
CPU variant	: 0x0
CPU part	: 0xb76
CPU revision	: 7

Hardware	: BCM2708
Revision	: 000e
Serial		: 0000000017b769d5
```

Write down the number in the line "Serial". Make sure that your power supply matches the power demands of the Raspberry Pi and its connected devices. A standard USB charger may not be sufficient to connect additional devices as well as a modem. The most simple solution is to use a USB hub that provides power on both the host and client ports, such as [this one](http://www.logilink.eu/showproduct/UA0085.htm). Connect the Raspberry Pi to the host side and all other devices to the client sides.

## Installation and registration

Log in to the Raspberry Pi and install the agent.

```shell
	$ wget http://resources.cumulocity.com/examples/cumulocity-rpi-agent-latest.deb
	$ sudo dpkg -i cumulocity-rpi-agent-latest.deb
```

Open Cumulocity in a web browser and go to the "Registration" page. Enter the serial number that you wrote down in the previous step and click "Register Device".

![Register device](images/deviceregistration.png)

> Note: If you are running a dedicated edition of the Cumulocity platform you will need to configure the host in /usr/share/cumulocity-rpi-agent/cfg/cumulocity.properties and restart the agent using:
>
>	$ sudo service cumulocity-agent restart

After that accept the registration.

![Accept device](images/deviceacceptance.png)

Click on "All devices" to manage the Raspberry Pi. It is by default visible as "RaspPi <<hardware model>> <<serial number>>". You can edit the name in the "Info" tab.

## Tinkerforge bricks and bricklets

The agent supports Tinkerforge devices out of the box, provided the [Tinkerforge daemon for Raspberry Pi](http://www.tinkerforge.com/de/doc/Embedded/Raspberry_Pi.html) is installed.

## PiFace Digital

The agent includes a simple [PiFace Digital](http://www.element14.com/community/docs/DOC-52857/l/piface-digital-for-raspberry-pi) driver. The driver will create events when switches are pressed and will react to remote control commands to the relays.

Before using your PiFace Digital make sure you have tested it following the instructions on the [official website](http://www.piface.org.uk/guides/Install_PiFace_Software/). You don't have to go through the whole guide. Following it up to the "Testing your PiFace" part is enough.

## Installing a 3G modem

There are [numerous descriptions available](http://www.thefanclub.co.za/how-to/how-setup-usb-3g-modem-raspberry-pi-using-usbmodeswitch-and-wvdial) for installing a 3G modem on a Raspberry Pi. An affordable 3G modem that works with Linux and that we tested with the Raspberry Pi is the Telekom Speedstick Basic (Huawei E3131).

When using a 3G modem with a Raspberry Pi Model B, a powered USB hub is required. Some modems will not read connectivity statistics concurrently to being dialed up to the Internet on the Raspberry Pi. Hence, this functionality is disabled by default in the Cumulocity Linux modem driver.

## Remote firmware upgrade

The agent permits you to upgrade the firmware of a Raspberry Pi through the [rpi-update](https://github.com/Hexxeh/rpi-update) tool. To configure a firmware version:

* Open Cumulocity and click on "Firmware".
* Click "Add Firmware".
* Enter a name for the firmware. As URL, use the Git hash of the firmware version at https://github.com/Hexxeh/rpi-firmware. (I.e., click on commits and select a particular version there. The hash is the weird garbage at the end of the URL.)
* Save the firmware version.

To roll out the firmware to a Raspberry Pi:

* Click on the "Software" tab of the Raspberry Pi.
* Click "Install firmware".
* Select the firmware version to install.
* Click "Install".

The Raspberry Pi will install the firmware and will reboot. Go to the "Control" tab to follow the upgrade process. After reboot, the operation will be either "Successful" or "Failed". Good luck.

## Troubleshooting

The agent writes debug information to the Pi's syslog. To troubleshoot, for example, connectivity problems, use:

```shell
$ tail -f /var/log/syslog
```
