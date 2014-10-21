Cumulocity Door Opener Demo with Cinterion EHS5 and YAWiD M.A.J.A.
==================================================================

This demo leverages Cumulocity's technology to open a door from any place in the world that provides Internet connectivity.

At its core, the demo runs on a YAWiD M.A.J.A. The M.A.J.A. is an embedded device with a Gemalto M2M Cinterion EHS5 chip. This chip runs J2ME Java-based applications and connects to the Internet via a GSM/UMTS connection.

M.A.J.A. includes programmable input and output ports to which a *Burg Wächter TSE 6202 Switch* radio sender is connected. This radio sender receives an electrical signal from M.A.J.A and in turn sends out a radio signal to a paired *Burg Wächter TSE 6000 System* door handle. This battery-powered door handle in turn reacts on authorized radio signals and unlocks its cylinder lock for a couple of seconds, allowing entry during this time.

Go through the following steps to have your own working remote door opener:

1. Install the *Gemalto EHSx Software Development Kit*.
2. Let Cumulocity know you would like to use this demo.
3. Pair the *Burg Wächter TSE 6202 Switch* radio sender with the *Burg Wächter TSE 6000 System* door handle.
4. Wire up the radio sender with the M.A.J.A. device.
5. Check out this demo's repository.
6. Import this repository into Gemalto M2M's Eclipse IDE distribution.
7. Build the door opener demo to generate required J2ME artifacts (JAD an JAR files).
8. Modify the JAD file to allow autostarting of the door opener demo on M.A.J.A. restarts.
9. Create an `apn_config.txt` file
10. Copy the application **and** your custom configuration files onto your M.A.J.A. device.
11. Sanity-check that you can connect to the M.A.J.A. device over a serial terminal session.
12. Connect to the M.A.J.A. device via a serial terminal to configure start-up behavior.
13. Register the M.A.J.A. device with your Cumulocity host.
14. Add the Apartmentmanagement plugin to your Cumulocity host.
15. Install the door opener demo application onto your M.A.J.A. device.
16. Restart the M.A.J.A. device and wait for the device to bootstrap-register with your host
17. Open the door

In the following, we go through each of the above mentioned steps in detail.


Install the *Gemalto EHSx Software Development Kit*
===================================================

As the door opener demo uses libraries and tools that are available through the *Gemalto EHSx Software Development Kit*, you will need to have this Windows-based SDK installed. Contact Gemalto M2M or YAWiD to obtain this SDK.


Let Cumulocity know you would like to use this demo
===================================================

Let Cumulocity know that you would like to use the door opener demo by sending an e-mail to [info@cumulocity.com](mailto:info@cumulocity.com?subject=door-opener-demo). We will then provide you with a file `cumulocity_config.txt`. This file will include information about your own Cumulocity host (e.g. `myhost.cumulocity.com`) and bootstrap credentials used for the initial device registration with your platform host. This file is needed later during step *Copy the application **and** your custom configuration files onto your M.A.J.A. device*.

Additionally, we will provide you with instructions on how to prepare `yourhost.cumulocity.com` for this demo.

Pair the *Burg Wächter TSE 6202 Switch* radio sender with the *Burg Wächter TSE 6000 System* door handle.
=========================================================================================================

Burg Wächter provides additional hardware to pair radio senders with door handles. Your Burg Wächter distributor can probably pair your devices for a small charge without you having to buy any pairing hardware.


Wire up the radio sender with the M.A.J.A. device.
==================================================

The *Burg Wächter TSE 6202 Switch* radio sender has to be connected with the M.A.J.A. device.

The radio sender has three cables: red, black, and blue.

* Connect the red cable to a suitable plus voltage source, e.g. directly to the same voltage source that supplies the M.A.J.A. device.
* Connect the black cable to a suitable minus voltage source , e.g. directly to the same minus voltage source that supplies the M.A.J.A. device.
* Connect the blue cable to the lower port of M.A.J.A.'s output port 2.
* Add to this setup an additional cable: This cable connects the upper port of M.A.J.A.'s output port 2 to a suitable minus voltage source, e.g. directly to the same minus voltage source that supplies the M.A.J.A. device.


Check out this demo's repository
================================
This demo uses a Mercurial repository. Clone this demo to your computer with the following command:

    hg clone https://bitbucket.org/m2m/cumulocity-examples

In that repository, the door opener demo is located within the subfolder `door-opener-demo`. 


Import this repository into Gemalto M2M's Eclipse IDE distribution
==================================================================

You can build the Cumulocity Door Opener Demo from sources. As the J2ME-based Cinterion EHS5 device is tightly coupled with its Gemalto EHSx SDK, this demo uses for its build tooling an Eclipse Project that can be imported into the SDK-provided Eclipse distribution.

After you have cloned this demo's repository onto your local computer, you can import its contained Eclipse Project into the SDK-provided Eclipse distribution following these steps:

1. From Eclipse's toolbar, select *File* -> *Import...*.
2. In the *Import* dialog, expand the *General* category and choose *Existing Projects into Workspace*. Click *Next >*.
3. Have *Select root directory:* selected and click *Browse...*.
4. Navigate to the folder into which you checked out the `cumulocity-examples` repository, from there into subfolder `door-opener-demo`, and click *OK*.
5. Back in the *Import* dialog, there should now be a selected project named *DoorOpenerDemo*. Click *Finish*.

Eclipse's Package Explorer will now include the *DoorOpenerDemo* project.


Build the door opener demo to generate required J2ME artifacts (JAD an JAR files)
=================================================================================

Once you have successfully imported the demo, you can generate the JAR and JAD files required for running the demo on a M.A.J.A. device:

1. In the Package Explorer view, right-click the door opener demo project.
2. Select *Export...*.
3. Expand the *Java ME* category, select "Export MIDlet Package", then click "Next >".
4. In the *MIDlet Package Export* dialog, select *DoorOpenerDemo* and *Use deployment directory*. *IMP_NG_EHS5_REMOTE1* may or may not be selected. Click "Finish".
5. Use a file manager to browse to the **imported** door opener demo Eclipse Project folder. Here, you will now find a subfolder named `deployed\IMG_NG_EHS5_REMOTE1`. Inside it, you will find two files, `DoorOpenerDemo.jad` and `DoorOpenerDemo.jar`.


Modify the JAD file to allow autostarting of the door opener demo on M.A.J.A. restarts
======================================================================================

For the door opener demo to automatically run when the M.A.J.A. device is turned on, make sure the `DoorOpenerDemo.jad` file includes the following lines:

    Oracle-MIDlet-Autostart: 1
    Oracle-MIDlet-Restart: false
    Oracle-MIDlet-Restart-Count: 5

Also make sure that the `DoorOpenerDemo.jad` file ends with a `\r\n` newline.


Create an `apn_config.txt` file
===============================

As the door opener project requires Internet accessibility via GSM or UMTS, you will require an installed SIM card in the M.A.J.A. device.

Every SIM card operator has their individual APN configuration and so this demo needs to know about the configuration that is applicable to the SIM card used in your installation. This demo therefore expects a file `apn_config.txt` in the Cinterion chip's filesystem root directory.

On your computer, create the `apn_config.txt` file and fill it with a single line that corresponds to the following structure:

    ;bearer_type=gprs;access_point=<apn>;username=<some_username>;password=<some_password>

Some SIM card operators don't require you to provide a username and password. In that case you can provide random data. For instance, the following file content is adequate for use with *Tele2 M2M* SIM cards:

    ;bearer_type=gprs;access_point=m2m.tele2.com;username=anyone;password=something


Copy the application **and** your custom configuration files onto your M.A.J.A. device
======================================================================================

You will require to copy files onto YAWiD M.A.J.A.'s Cinterion EHS5 chip in order to install the door opener demo application and your individual configuration data. To do so, you will use tools installed during the Gemalto EHSx SDK installation. The below subsections show you two ways of interacting with files within the Cinterion chip file system.

you will need to copy the following files into the Cinterion chip's filesystem:

* `DoorOpenerDemo.jad` (edited to include the additional autostart lines as mentioned in chapter *Modify the JAD file to allow autostarting of the door opener demo on M.A.J.A. restarts*)
* `DoorOpenerDemo.jar`
* `cumulocity_config.txt`
* `apn_config.txt`

Copying files via GUI
---------------------

After you have installed the Gemalto EHSx SDK, Windows' File Explorer will show a device named "Module". With this, you can use the usual File Explorer mechanisms such as drag and drop to copy files from your computer into the Cinterion chip's file system.

Copying files via command line
-----------------------------

After you have installed the Gemalto EHSx SDK, your Windows environment will have the command `MEScopy` within its `PATH`. It can be run from a shell such as Command Prompt or Cygwin. For example, to copy `some_file` from the current working directory into the Cinterion chip's root directory, run the following command.

    MEScopy some_file mod:a:some_file


Sanity-check that you can connect to the M.A.J.A. device over a serial terminal session
=======================================================================================

It is possible to connect to the M.A.J.A. via a serial connections. Under Windows, you can use a free and open-source tool such as PuTTY to do so. Once the M.A.J.A. device is connected to a Windows computer via its USB interface, Windows' device manager will list several COM ports. From Cumulocity's experience, the Cinterion chip's *Port3* and *Port4* work well for serial terminal communication (speed 115200 baud, data bits 8, stop bits 1, parity none, flow control RTS/CTS or hardware).

Once you have an open serial terminal session, sanity-check that the serial connection works well by writing the following AT command

    AT+CGSN

then press return.
If the serial connection works well, the Cinterion chip will return with the device's IMEI, a newline, and a line saying OK:

    123456789012345
    
    OK

Keep in mind that when you send an AT command and get an `ERROR` response in return, then this does not necessarily mean that something is wrong with the command you sent - some stale bytes might have still been in the AT command data pipe. Try to run the command a second time and see if it runs successfully and the Cinterion chip now returns an `OK` response.


Connect to the M.A.J.A. device via a serial terminal to configure start-up behavior
===================================================================================

For the door opener demo application to work autonomously, The M.A.J.A. device has to be configured to autostart the Cinterion chip on power up and also needs to be configured to autostart applicable MIDlets.

When the M.A.J.A. device powers up, with the default factory settings the Cinterion chip is not autostarted.

The Cinterion chip gets activated by the containing M.A.J.A. device. You can observe the different start up states by taking a look at M.A.J.A.'s *PWR* LED:

* If power is provided and the *PWR* LED is turned on, then this means the M.A.J.A. device is turned on and the Cinterion chip is turned off. 
* If power is provided and the *PWR* LED is turned off, then this means both the M.A.J.A. device and the Cinterion chip are turned on.

The Cinterion chip is activated in any of the following ways, among others:

* when providing M.A.J.A.'s *IGT* ignition port with a suitable voltage,
* or when plugging in a *hot* USB cable,
* or when configuring one of the M.A.J.A. device control registers to *autostart* the Cinterion device on power up.

You can set the M.A.J.A. device autostart control register with the following procedure.

First, connect to the M.A.J.A. device via a serial terminal connection, e.g. by using PuTTY as explained in section *Sanity-check that you can connect to the M.A.J.A. device over a serial terminal session*. Once connected on a serial terminal connection, run the following AT command:

    AT^SSPI=0000

Press return. The `AT^SSPI=0000` command opens the Cinterion device's I2C bus and will interpret all following input as I2C commands until the I2C connection is explicitly closed.

Checking the current Cinterion chip start configuration
-----------------------------------------------------------

Once you are in an active I2C connection, send the following I2C command:

    <a20630601>

Press return. What does the `<a20630601>` command do? Everything inside the `< >` angle brackets represents the following I2C command: "Write (`20`) to M.A.J.A.s configuration area (`63`) the following command: read out beginning from address *YMC_OPR_CTRL1* (`06`) a total of `01` bytes."
After sending this data, the Cinterion chip will respond with `{a+}`.

Now send the following command:

    <a210005>

Press return. What does the `<a210005>` command do? "Read (`21`) a total of `0005` bytes: 4 byte metadata and 1 byte requested payload from the previous command.

The M.A.J.A. device should return the following data:

    {a+430601xxyy}

where `xx` is the XORed value of `yy`. `yy` represents the current data in M.A.J.A.'s *YMC_OPR_CTRL1* control register. `yy`'s factory default value is `0C`, therefore you will probably see `{a+430601OCOC}` on factory default configuration. `0C` stands for:

* No power-up delay.
* If a *hot* USB cable plugs into the USB port, then turn on the Cinterion chip.
* If there is a signal edge on DTR, then turn on the Cinterion chip.
* If there is a *hot* USB cable _already_ plugged in at the moment the M.A.J.A. device is powered up, don't necessarily turn on the Cinterion chip.
* If the M.A.J.A. device is powered up, then don't necessarily turn on the Cinterion chip.

Setting the M.A.J.A. device to autostart the Cinterion chip on power up
-----------------------------------------------------------------------

Once you are in an active I2C session, send the following I2C command:

    <a20430601000F>

Press return. The Cinterion device will return `{a+}`. What did this command do? "Write (`20`) into the configuration area's (`43`) *Operation Control Register #1* (*YMC_OPR_CTRL1*, `06)`) a total of `01` bytes: (we don't care about any CRC check - `00`): namely write as data a single byte (hexadecimally encoded) `0F` (this equals binary *0000 1111*).

In particular, this will set the following register flags:

* No power-up delay.
* If a *hot* USB cable plugs into the USB port, then turn on the Cinterion chip.
* If there is a signal edge on DTR, then turn on the Cinterion chip.
* If there is a *hot* USB cable _already_ plugged in at the moment the M.A.J.A. device is powered up, then turn on the Cinterion chip.
* If the M.A.J.A. device is powered up, then turn on the Cinterion chip.

You can check that this new configuration was accepted by going through the steps in subsection *Checking the current Cinterion chip start configuration* again, this time expecting a `yy` value of `0F`, i.e. the Cinterion chip will return `{a+4306010F0F}`

Close the I2C session by sending `#`.

Activate MIDlet autostart
------------------------ 

Also the Cinterion chip needs to be configured to autostart applicable MIDlets. We have configured the door opener demo applicable for autostarting with the configuration done in chapter *Modify the JAD file to allow autostarting of the door opener demo on M.A.J.A. restarts*. 

Open a serial terminal connection to the Cinterion chip.
Send the following AT command:

    AT^SCFG=?

This will list all available configuration options of this Cinterion chip. We are interested in configuring `Userware/Autostart`.

Now send the following AT command:

    AT^SCFG?

This will list the current configuration of this Cinterion chip. For the `Userware/Autostart` configuration, the factory default setting is `0`, meaning that applicable MIDlets are not autostarted.

To activate autostarting applicable MIDlets, send the following AT command:

    ATSCFG="Userware/Autostart","","1"

Also set a delay of 10 seconds before MIDlet autostart happens. This gives a time window in which you can cancel autostarting in exceptional situations where the MIDlet misbehaves (the `100` parameter stands for 100 * 100ms = 10 seconds):

    ATSCFG="Userware/Autostart/Delay","","100"

Register the M.A.J.A. device with your Cumulocity host
======================================================

Visit your host in a web browser, e.g. visit `http://myhost.cumulocity.com`. In the *Devicemanagement* app, in the left pane, select *Registration*. You are now in the *Device Registration* dialog. Here, provide as *Device ID* your M.A.J.A.'s IMEI number, e.g. `123456789012345`. You can find this number on the device's bottom. Click *Register Device*. The IMEI will now be listed on the device registration page with status *WAITING FOR CONNECTION*. 


Add the Apartmentmanagement plugin to your Cumulocity host
==========================================================

You will receive information on how to add the Apartmentmanagement plugin to `yourhost.cumulocity.com` when you send an e-mail to Cumulocity as explained in *Let Cumulocity know you would like to use this demo*.


Install the door opener demo application onto your M.A.J.A. device
==================================================================

After you have copied all required files onto the Cinterion chip, the application still has to be explicitly installed.
Open a terminal session to the Cinterion chip.

Install the copied application by running the following command:

    AT^SJAM=0,"a:/DoorOpenerDemo.jad",""

The next time you turn off and turn back on the M.A.J.A. device, the door opener application will run automatically.


Restart the M.A.J.A. device and wait for the device bootstrap-register with your host
=====================================================================================

After the application installation, power off and power on again the M.A.J.A. device. The configured autostart behaviors will automatically power up the Cinterion device, then automatically start the door opener demo application.

Every time the application starts up, in the beginning you can see M.A.J.A.'s LED 1, LED 2, and LED 3 blink through once.

While the application sets up the device, either during bootstrapping or normal start up procedure, LED 1 is turned on. When these phases are over, LED 1 turns off.

When the application starts up for the first time, it will go through the so-called bootstrap procedure. During this procedure, the application will ask your Cumulocity host for unique device credentials and then persist this information in a file named `device_user_config.txt` in the Cinterion chip's file system root. Also during the bootstrapping procedure, the application will create "apartment" and "button" objects in your Cumulocity host's database, then append their IDs to `device_user_config.txt`.

While the M.A.J.A. device is bootstrapping, it will poll your Cumulocity host for device credentials. You will notice this in your web browser on the *Devicemanagement* app's *Device Registration* page: the M.A.J.A. device entry will change its status from *WAITING FOR CONNECTION* to *PENDING ACCEPTANCE*. Once this status change happened, click on the "Accept" button. This will create *device user* credentials on your Cumulocity host that are unique to this M.A.J.A. device. At M.A.J.A.'s next bootstrap poll cycle, your Cumulocity host will respond to the M.A.J.A. device with these credentials, completing the *device user credentials creation phase*.

Once the bootstrapping procedure finished and file `device_user_config.txt` has been written, the application will no longer need to go through the bootstrap procedure - from now on when the M.A.J.A. device gets turned off and turned on again, it will notice and read the existing file `device_user_config.txt`, skip bootstrapping, and directly go into *open door command listening mode*.


Open the door
=============

On `yourhost.cumulocity.com`, switch to app *Apartmentmanagement*. There, in the *Apartments* category, you should see an entry for your registered M.A.J.A. device with its IMEI, e.g. *Apartment123456789012345*. It includes a button *Open Door*. Clicking this button creates a *open door* device operation, for which the M.A.J.A. device is listening for. Once this operation has arrived at the M.A.J.A. device, it will activate its output port 2, causing the radio sender to send out a radio signal. The door handle will receive this signal, and unlock its cylinder lock. The door handle will indicate a received and authenticated radio signal by flashing its LED. It will also flash its LED after a few seconds again to indicate that the cylinder lock has been locked again.

While the output port 2 is active, M.A.J.A.'s LED 2 lights up. 


Cleaning up your Cumulocity host from everything door opener demo-related
=========================================================================
Have a look into file `device_user_config.txt` on the M.A.J.A. device. In this file, the second line is the `apartmentId`, the third line is the `buttonId`/`doorId`.

Delete the button by sending an HTTP DELETE request with Authorization header for the `buttonId`:

    yourhost.cumulocity.com/inventory/managedObjects/{buttonId}

Delete the apartment by sending an HTTP DELETE request with Authorization header for the `apartmentId`:
    yourhost.cumulocity.com/inventory/managedObjects/{apartmentId}

Delete the M.A.J.A. device user. In a web browser, visit `yourhost.cumulocity.com`, click *App Switcher* and select *Administration*. Now in the *Administration* app, go to "Device users". In this list, locate an entry *device_{imeiNumber}*, e.g. *device_123456789012345*. When hovering over this entry, a red *X* button will appear. Click on this button to delete this device user. A pop-up *Confirm delete?* will appear. Click *OK*.

Delete the SmartREST template by sending an HTTP DELETE request with Authorization header for the SmartREST template.


Cleaning up the YAWiD M.A.J.A. device from everything door opener demo-related
==============================================================================

Open a terminal session to the M.A.J.A. device. On the terminal session, run the following AT command to uninstall the door opener demo application:

    AT^SJAM=3,"a:/DoorOpenerDemo.jad",""

In case the app uninstalled successfully, the Cinterion chip will return `OK`. 


Finally, delete files

* `DoorOpenerDemo.jad`
* `DoorOpenerDemo.jar`
* `cumulocity_config.txt`
* `apn_config.txt` and
* `device_user_config.txt`

from the device using the `MESdel.exe` command or using Windows' File Explorer "Module" entry. To make sure that these files are deleted, run the `MESdel.exe` command several times for each file and turn the device off and on again.

    MESdel mod:a:device_user_config.txt

