
/*
 * YAWiD electronics provides this code for informational purpose only, "AS IS"
 * and without any warranty of any kind.
 * Any use is at your own risk.
 * 
 * Please consider that parts of the code are based on sources of the Gemalto
 * M2M GmbH. Please consider the following licenses and limitations of the
 * Gemalto M2M GmbH. 
 *
 * Copyright (C) Gemalto M2M GmbH 2013. All Rights reserved.
 *
 * Gemalto M2M GmbH ("Gemalto") grants Licensee a non-exclusive,
 * non-transferable, limited license to transmit, reproduce, disseminate, utilize
 * and/or edit the source code of this Software (IMlet, LIBlet, batch files,
 * project files) for the sole purpose of designing, developing and testing
 * Licensee's applications only in connection with a Gemalto Wireless Module.
 *
 * THIS CODE AND INFORMATION IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 *
 * GEMALTO, ITS LEGAL REPRESENTATIVES AND VICARIOUS AGENTS SHALL - IRRESPECTIVE
 * OF THE LEGAL GROUND - ONLY BE LIABLE FOR DAMAGES IF THE DAMAGE WAS CAUSED
 * THROUGH CULPABLE BREACH OF A MAJOR CONTRACTUAL OBLIGATION (CARDINAL DUTY),
 * I.E. A DUTY THE FULFILMENT OF WHICH ALLOWS THE PROPER EXECUTION OF THE
 * RESPECTIVE AGREEMENT IN THE FIRST PLACE OR THE BREACH OF WHICH PUTS THE
 * ACHIEVEMENT OF THE PURPOSE OF THE AGREEMENT AT STAKE, RESPECTIVELY, AND ON THE
 * FULFILMENT OF WHICH THE RECIPIENT THEREFORE MAY RELY ON OR WAS CAUSED BY GROSS
 * NEGLIGENCE OR INTENTIONALLY. ANY FURTHER LIABILITY FOR DAMAGES SHALL -
 * IRRESPECTIVE OF THE LEGAL GROUND - BE EXCLUDED. IN THE EVENT THAT GEMALTO IS
 * LIABLE FOR THE VIOLATION OF A MAJOR CONTRACTUAL OBLIGATION IN THE ABSENCE OF
 * GROSS NEGLIGENCE OR WILFUL CONDUCT, SUCH LIABILITY FOR DAMAGE SHALL BE LIMITED
 * TO AN EXTENT WHICH, AT THE TIME WHEN THE RESPECTIVE AGREEMENT IS CONCLUDED,
 * GEMALTO SHOULD NORMALLY EXPECT TO ARISE DUE TO CIRCUMSTANCES THAT THE PARTIES
 * HAD KNOWLEDGE OF AT SUCH POINT IN TIME. GEMALTO SHALL IN NO EVENT BE LIABLE
 * FOR INDIRECT AND CONSEQUENTIAL DAMAGES OR LOSS OF PROFIT. GEMALTO SHALL IN NO
 * EVENT BE LIABLE FOR AN AMOUNT EXCEEDING € 20,000.00 PER EVENT OF DAMAGE. WITHIN
 * THE BUSINESS RELATIONSHIP THE OVERALL LIABILITY SHALL BE LIMITED TO A TOTAL
 * OF € 100,000.00. CLAIMS FOR DAMAGES SHALL BECOME TIME-BARRED AFTER ONE YEAR AS
 * OF THE BEGINNING OF THE STATUTORY LIMITATION PERIOD. IRRESPECTIVE OF THE
 * LICENSEE'S KNOWLEDGE OR GROSS NEGLIGENT LACK OF KNOWLEDGE OF THE CIRCUMSTANCES
 * GIVING RISE FOR A LIABILITY ANY CLAIMS SHALL BECOME TIME-BARRED AFTER FIVE
 * YEARS AS OF THE LIABILITY AROSE. THE AFOREMENTIONED LIMITATION OR EXCLUSION
 * OF LIABILITY SHALL NOT APPLY IN THE CASE OF CULPABLE INJURY TO LIFE, BODY OR
 * HEALTH, IN CASE OF INTENTIONAL ACTS, UNDER THE LIABILITY PROVISIONS OF THE
 * GERMAN PRODUCT LIABILITY ACT (PRODUKTHAFTUNGSGESETZ) OR IN CASE OF A
 * CONTRACTUALLY AGREED OBLIGATION TO ASSUME LIABILITY IRRESPECTIVE OF ANY
 * FAULT (GUARANTEE).
 *
 * IN THE EVENT OF A CONFLICT BETWEEN THE PROVISIONS OF THIS AGREEMENT AND
 * ANOTHER AGREEMENT REGARDING THE SOURCE CODE  OF THIS SOFTWARE (IMLET, LIBLET,
 * BATCH FILES, PROJECT FILES) (EXCEPT THE GENERAL TERMS AND CONDITIONS OF
 * GEMALTO) THE OTHER AGREEMENT SHALL PREVAIL.
 *
 */

/** =0 ========================================================================
 * contents
 * ----------------------------------------------------------------------------
 * 0	contents
 * I	Introduction
 * L	LED methods
 * W	Watchdog methods (and enabling the GPS receiver)
 * S	'Interrupt'
 * 2	I2C interface
 * A	ASC0 and ASC1
 * M	Initialization the M.A.J.A.
 * D	the DEMO main methods (constructor etc.) 
 * ========================================================================= */

/** =I ========================================================================
 *
 * Introduction
 *
 * ========================================================================== */
// ----------------------------------------------------------------------------
// The following code is an example for a Java MIDlet for the terminal M.A.J.A.
// 
// The methods and algorithms are simple and only examples. The intention is to
// demonstrate the main functions of M.A.J.A. in combination with a Java MIDlet
// running on the integrated wireless module.
//
// There is no guarantee that function does not be change in future versions!
// Please regard the actual user manuals, data sheets, application notes etc. 
//
// The structure of this code is:
//	- Methods for the LED, outputs, inputs, I2C, serial interfaces.
//    These methods describe the special functions of the M.A.J.A. hardware.
//	- the common Java methods 'constructor', 'startApp()', 'destroyApp' at the
//    end of this document.
// ----------------------------------------------------------------------------

package com.cumulocity.yawidmaja;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.microedition.io.CommConnection;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import com.cinterion.io.ATCommand;
import com.cinterion.io.I2cBusConnection;
import com.cinterion.io.InPort;

import com.cinterion.io.OutPort;
import com.cumulocity.me.util.IOUtils;
import com.cumulocity.me.util.StringUtils;

public class YawidMajaInitializer {

	public YawidMajaInitializer() {
		System.out.println("YawidMajaInitializer: constructor");
	}
	
	private final static String GPIO_LED1 = "GPIO20"; // Output to LED1 (active-high)
	private final static String GPIO_LED2 = "GPIO21"; // Output to LED2 (active-high)
	private final static String GPIO_LED3 = "GPIO22"; // Output to LED3 (active-high)

	private OutPort ledPort;
	
	private void initLedPort() {
		Vector ledPins = new Vector(3);
		Vector ledValues = new Vector(3);
		ledPins.addElement(GPIO_LED1);
		ledValues.addElement(Integer.valueOf("0")); // LED1
		ledPins.addElement(GPIO_LED2);
		ledValues.addElement(Integer.valueOf("0")); // LED2
		ledPins.addElement(GPIO_LED3);
		ledValues.addElement(Integer.valueOf("0")); // LED3
		try {
			ledPort = new OutPort(ledPins, ledValues);
		} catch (IOException e) {
			System.err.println(">>> initLedPort: " + e);
		}
	}

	private void releaseLedPort() {
		try {
			ledPort.release(); // release the ports
		} catch (IOException e) {
			System.err.println(">>> releaseLedPort: " + e);
		}
	}

	public void setLedPort(int ledMask) {
		try {
			// Read the current setting and OR it with the mask 'ledMask'.
			int ledStatus = (ledPort.getValue() | ledMask) & 0x07;
			ledPort.setValue(ledStatus);
		} catch (IOException e) {
			System.err.println(">>> setLedPort: " + e);
		}
	}

	public void clearLedPort(int ledMask) {
		try {
			// Read the current setting and mask-out the bits from 'ledMask'.
			int ledStatus = (ledPort.getValue() & ~ledMask) & 0x07;
			ledPort.setValue(ledStatus);
		} catch (IOException e) {
			System.err.println(">>> clearLedPort: " + e);
		}
	}

	public void setLed1() {
		setLedPort(0x01 << 0);
	};

	public void setLed2() {
		setLedPort(0x01 << 1);
	};

	public void setLed3() {
		setLedPort(0x01 << 2);
	};

	public void clearLed1() {
		clearLedPort(0x01 << 0);
	};

	public void clearLed2() {
		clearLedPort(0x01 << 1);
	};
	
	public void clearLed3() {
		clearLedPort(0x01 << 2);
	};


	private OutPort ctrlPort;

	private final static String GPIO_WDOG = "GPIO6";
	private final static String GPIO_GPS = "GPIO7";


	private void initCtrlPort() {
		Vector ctrlPins = new Vector(2);
		Vector ctrlValues = new Vector(2);
		ctrlPins.addElement(GPIO_WDOG);
		ctrlValues.addElement(Integer.valueOf("0"));
		ctrlPins.addElement(GPIO_GPS);
		ctrlValues.addElement(Integer.valueOf("0"));
		try {
			ctrlPort = new OutPort(ctrlPins, ctrlValues);
		} catch (IOException e) {
			System.err.println(">>> initCtrlPort: " + e);
		}
	}

	private void releaseCtrlPort() {
		try {
			ctrlPort.release(); // release the port
		} catch (IOException e) {
			System.err.println(">>> releaseCtrlPort: " + e);
		}
	}

	public void toggleWatchDog() {
		try {
			ctrlPort.setValue(ctrlPort.getValue() ^ 0x01); // read , toggle and
															// write the
															// watchdog pin
		} catch (IOException e) {
			System.err.println(">>> toggleWatchDog: " + e);
		}
	}

	private void enableGpsReceiver(boolean enableReceiver) {
		try {
			// read the port value and clear the GPIO for the GPS receiver
			int portValue = ctrlPort.getValue() & (~0x02);
			// If demanded set the bit to switch-on the GPS receiver.
			if (enableReceiver == true)
				portValue |= 0x02;
			ctrlPort.setValue(portValue); // set/reset the GPIO
		} catch (IOException e) {
			System.err.println(">>> enableGpsReceiver: " + e);
		}
	}

	private final static String GPIO_INT = "GPIO8";
	private InPort interruptInPort;
	private YawidMajaInterruptInPortListener yawidMajaInterruptInPortListener;

	private void initInterruptInPort() {
		Vector intPins = new Vector(1);
		intPins.addElement(GPIO_INT);
		try {
			interruptInPort = new InPort(intPins);
		} catch (IOException e) {
			System.err.println(">>> initIntPort: " + e);
		}
		
		yawidMajaInterruptInPortListener = new YawidMajaInterruptInPortListener(this);
		System.out.println("adding interruptInPortListener...");
		interruptInPort.addListener(yawidMajaInterruptInPortListener);
		
		System.out.println("triggering getInterruptStatus() once to activate InPort listening...");
		getInterruptStatus();
	}
	
	public InPort getInterruptPort() {
		return interruptInPort;
	}

	public YawidMajaInterruptInPortListener getYawidMajaInterruptInPortListener() {
		return yawidMajaInterruptInPortListener;
	}

	private void releaseInterruptInPort() {
		try {
			interruptInPort.release();
		} catch (IOException e) {
			System.err.println(">>> releaseIntPort: " + e);
		}
	}

	// returning 'true' means that the GPIO is LOW (an interrupt occurred).
	// returning 'false' means that the GPIO is HIGH and no interrupt is pending.
	public boolean getInterruptStatus() {
		try {
			return ((interruptInPort.getValue() & 0x01) == 0x00);
		} catch (IOException e) {
			System.err.println(">>> getIntStatus: " + e);
		}

		return true;
	}

	private I2cBusConnection i2cBusCon;
	private DataOutputStream i2cOutStream;
	private DataInputStream i2cInStream;

	private final static String I2C_WR_ADDRESS = "20";
	private final static String I2C_RD_ADDRESS = "21";

	private final static String YCP_RD_SECTION_CFG = "63";
	private final static String YCP_WR_SECTION_CFG = "43";

	private final static String YMC_WDOG_CTRL0 = "18"; // Watchdog Control Register #0
	private final static String YMC_GPS_CTRL0 = "20"; // GPS Control Register #0
	private final static String YMC_INP_CTRL1 = "29"; // Input Control Register #1
	private final static String YMC_INP_CTRL2 = "2A"; // Input Control Register #2

	private void initI2C() {
		try {
			i2cBusCon = (I2cBusConnection) Connector.open("i2c:0;baudrate=100");
			i2cInStream = i2cBusCon.openDataInputStream();
			i2cOutStream = i2cBusCon.openDataOutputStream();

		} catch (IOException e) {
			System.err.println(">>> initI2C: " + e);
		}
	}

	private void closeI2C() {
		try {
			i2cInStream.close();
			i2cOutStream.close();
			i2cBusCon.close();
		} catch (IOException e) {
			System.err.println(">>> closeI2C: " + e);
		}
	}


	// parameter sCmd is the I2C command without address
	// returning true means sending the command was successful
	// returning false means sending the command was unsuccesful
	public boolean sendI2CCommand(String sCmd) {
		try {
			// Make a little pause to guarantee that the last I2C transfer is
			// terminated. During this pause, clear any old responses.
			long start = System.currentTimeMillis();
			while ((System.currentTimeMillis() - start) < 100) {

				while (i2cInStream.available() > 0) {
					int readByteAsInt = i2cInStream.read();
					//System.out.println("killing a character: " + (char)(readByteAsInt + 48) );
					//System.out.println("killing a character: " + readByteAsInt + " / "+ (char) readByteAsInt );
				}
				
			}
			String i2cTxString = "<a" + I2C_WR_ADDRESS + sCmd + ">";
			i2cOutStream.write(i2cTxString.getBytes(), 0, i2cTxString.length());
			i2cOutStream.flush();
			// wait for the response
			start = System.currentTimeMillis();
			while (  ( System.currentTimeMillis() - start ) < 500  ) { // wait up to
																	// 500
																	// milliseconds
				while (i2cInStream.available() > 0) { // just simple: we wait
														// for the '0' in
														// "{a+}".
					switch (i2cInStream.read()) {
					case '+':
						return true;
					case '-':
						System.err.println("sendI2cCmd: '" + sCmd + "' fails!");
						return false;
					}
				}
			}
			System.out.println("sendI2cCmd: '" + sCmd + "' no response!");
		} catch (Exception e) {
			System.out.println(">>> sendI2cCmd: " + e);
		}
		return false;
	}

	public String getI2CData(int nbrOfBytes) {
		try {
			// Make a little pause to guarantee that the last I2C transfer is
			// terminated. During this pause, clear any old responses.
			long start = System.currentTimeMillis();
			while ((System.currentTimeMillis() - start) < 100) {
				while (i2cInStream.available() > 0)
					i2cInStream.read();
			}
			// Convert the number of bytes to read to 4 digits hex:
			String i2cTxString = Integer.toHexString(nbrOfBytes);
			while (i2cTxString.length() < 4)
				i2cTxString = "0" + i2cTxString;
			// Create the I2C read command:
			i2cTxString = "<b" + I2C_RD_ADDRESS + i2cTxString + ">";
			i2cOutStream.write(i2cTxString.getBytes(), 0, i2cTxString.length());
			i2cOutStream.flush();
			// wait for the response
			StringBuffer response = new StringBuffer(100);
			start = System.currentTimeMillis();
			while ((System.currentTimeMillis() - start) < 500) { // wait up to
																	// 500
																	// milliseconds
				while (i2cInStream.available() > 0) { // just simple: we wait
														// for "{" and "}"
					response.append((char) i2cInStream.read());
					if (response.charAt(0) != '{')
						response.setLength(0);
					if (response.length() > 3) {
						if (response.charAt(response.length() - 1) == '}')
							return response.toString();
					}
				}
			}
			System.out.println("getI2cData: no response (" + response + ")!");
		} catch (Exception e) {
			System.out.println(">>> getI2cData: " + e);
		}
		return "";
	}


	private CommConnection commConnASC0, commConnASC1;
	private InputStream inStreamASC0, inStreamASC1;
	private OutputStream outStreamASC0, outStreamASC1; 

	//ASC0 == Y.A.W.I.D. Maja's RS-232 DE-9 connector
	private void initASC0(int baudrate) {
		try {
			String strCOM = "comm:COM0;blocking=on;baudrate=" + baudrate;
			commConnASC0 = (CommConnection) Connector.open(strCOM);
			inStreamASC0 = commConnASC0.openInputStream();
			outStreamASC0 = commConnASC0.openOutputStream();
			System.out.println("ASC0 initalized with "
					+ commConnASC0.getBaudRate() + " bps.");
		} catch (IOException e) {
			System.out.println(">>> initASC0: " + e);
		}
	}

	private void closeASC0() {
		try {
			inStreamASC0.close();
			outStreamASC0.close();
			commConnASC0.close();
		} catch (IOException e) {
			System.out.println(">>> initASC0: " + e);
		}
	}

	public void sendASC0(String stringToSend) {
		try {
			outStreamASC0.write(stringToSend.getBytes(), 0,
					stringToSend.length());
		} catch (IOException e) {
			System.out.println(">>> sendASC0: " + e);
		}
	}

	public String readASC0() {
		StringBuffer receivedData = new StringBuffer(100);
		try {
			while (inStreamASC0.available() > 0) {
				receivedData.append((char) inStreamASC0.read());
				if (receivedData.capacity() == 0)
					return receivedData.toString();
			}
			return (receivedData.toString());
		} catch (IOException e) {
			System.out.println(">>> readASC0: " + e);
		}
		return "";
	}

	private void initASC1(int baudrate) {
		try {
			String strCOM = "comm:COM1;blocking=on;baudrate=" + baudrate;
			commConnASC1 = (CommConnection) Connector.open(strCOM);
			inStreamASC1 = commConnASC1.openInputStream();
			outStreamASC1 = commConnASC1.openOutputStream();
			System.out.println("ASC1 initalized with "
					+ commConnASC1.getBaudRate() + " bps.");
		} catch (IOException e) {
			System.out.println(">>> initASC1: " + e);
		}
	}

	private void closeASC1() {
		try {
			inStreamASC1.close();
			outStreamASC1.close();
			commConnASC1.close();
		} catch (IOException e) {
			System.out.println(">>> initASC1: " + e);
		}
	}

	public void sendASC1(String stringToSend) {
		try {
			outStreamASC1.write(stringToSend.getBytes(), 0,
					stringToSend.length());
		} catch (IOException e) {
			System.out.println(">>> sendASC1: " + e);
		}
	}

	public String readASC1() {
		StringBuffer receivedData = new StringBuffer(100);
		try {
			while (inStreamASC1.available() > 0) {
				receivedData.append((char) inStreamASC1.read());
				if (receivedData.capacity() == 0)
					return (receivedData.toString());
			}
			return (receivedData.toString());
		} catch (IOException e) {
			System.out.println(">>> readASC1: " + e);
		}
		return ("");
	}

	public void initYawidMaja() {
		initLedPort(); // Initialize the LEDs
		initCtrlPort(); // Initialize the control signals
		initInterruptInPort(); // Initialize the 'interrupt'
		initI2C(); // Initialize the I2C connection
		initASC0(115200); // Initialize ASC0 with 115200 bps
		initASC1(9600); // Initialize ASC1 with 9600 bps

		clearLedPort(0x07); // Clear all LEDs
		sendI2CCommand("A900"); // switch-off all output-relays

		// disable the watchdog function, because this code is used by the IDE
		sendI2CCommand(YCP_WR_SECTION_CFG // Command to read the 'configuration'
				+ YMC_WDOG_CTRL0 // Address of the watchdog control register
				+ "01" // 1 data byte follows
				+ "00" // no CRC
				+ "00"); // disable any watchdog functions

		// Enable the 'interrupt' for rising signals at all inputs:
		sendI2CCommand(YCP_WR_SECTION_CFG // Section 'Configuration'
				+ YMC_INP_CTRL1 // Address of the input control register #1
				+ "01" // 1 data byte follows
				+ "00" // no CRC
				+ "FF"); // enable all inputs

		// Enable the 'interrupt' for falling signals at all inputs:
		sendI2CCommand(YCP_WR_SECTION_CFG // Section 'Configuration'
				+ YMC_INP_CTRL2 // Address of the input control register #2
				+ "01" // 1 data byte follows
				+ "00" // no CRC
				+ "FF"); // enable all inputs

		// Enable the always-on function for the GPS receiver.
		sendI2CCommand(YCP_WR_SECTION_CFG // Section 'Configuration'
				+ YMC_GPS_CTRL0 // Address of the GPS control register #0
				+ "01" // 1 byte data byte follows
				+ "00" // no CRC
				+ "01"); // enable 'always-on' for the GPS receiver

		// Some of the implemented methods are not used in this document.
		// The IDE would give warnings about "never used methods".
		// These warnings do not occur because of the following condition.
		boolean skip = false;
		if (skip) {
			enableGpsReceiver(true); // switch-on the GPS receiver
		}
	
	}

	public void closeOutput(int outputNumber) {
		
		switch (outputNumber) {
		case 1:
			sendI2CCommand("A1");
			break;
		case 2:
			sendI2CCommand("A3");
			break;
		case 3: 
			sendI2CCommand("A5");
			break;
		case 4:
			sendI2CCommand("A7");
			break;
		default:
			System.err.println("non-existing output number specified");
			break;
		
		}
	}
	
	public void openOutput(int outputNumber) {
		
		switch (outputNumber) {
		case 1:
			sendI2CCommand("A0");
			break;
		case 2:
			sendI2CCommand("A2");
			break;
		case 3: 
			sendI2CCommand("A4");
			break;
		case 4:
			sendI2CCommand("A6");
			break;
		default:
			System.err.println("non-existing output number specified");
			break;
		
		}
	}
	
	public String getImei() throws Exception {
		ATCommand atCommand = null;
		try {
			String imeiString;
			atCommand = new ATCommand(false);
			String response = atCommand.send("AT+CGSN\r");

			String[] splittedResponse = StringUtils.split(response, "\r\n");
			imeiString = splittedResponse[1];

			return imeiString;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			closeConnection(atCommand);	
		}
	}
	

	
	public String getImsi() throws Exception {
		ATCommand atCommand = null;
		try {
			String imsiString;
			atCommand = new ATCommand(false);
			String response = atCommand.send("AT+CIMI\r");

			String[] splittedResponse = StringUtils.split(response, "\r\n");
			imsiString = splittedResponse[1];

			return imsiString;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		} 
		finally {
			closeConnection(atCommand);	
		}
	}
	
	public boolean fileExists(String filePathName) throws Exception {
		FileConnection fileConnection = null;
		try {
			boolean fileExists;
			fileConnection = (FileConnection) Connector.open("file:///a:/" + filePathName);
			fileExists = fileConnection.exists()? true : false;

			return fileExists;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			IOUtils.closeQuietly(fileConnection);
		}
	}
	
	public String readPlainTextFile(String filePathName) throws Exception {
		FileConnection fileConnection = null;
		try {
			if ( fileExists(filePathName) ) {
				fileConnection = (FileConnection) Connector.open("file:///a:/" + filePathName);
				InputStream is = fileConnection.openInputStream();
				String fileContent = readStringFromInputStream(is);
				return fileContent;
			}
			else {
				throw new Exception("plain text file " + filePathName + " does not exist ");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			IOUtils.closeQuietly(fileConnection);
		}
		 
	}
	
	public void writeOrOverwritePlainTextFile(String filePathName, String plainText) throws Exception {
		FileConnection fileConnection = null;
		try {
			fileConnection = (FileConnection) Connector.open("file:///a:/" + filePathName);
			if ( fileExists(filePathName) ) {
				fileConnection.delete();
				// API specification for delete() is: "The FileConnection instance object remains open and available for use."
				
			}
			fileConnection.create();
			
			OutputStream os = fileConnection.openOutputStream();
			writeStringToOutputStream(plainText, os);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			IOUtils.closeQuietly(fileConnection);
		}
		 
	}
	

	public void closeYawidMaja() {
		releaseLedPort(); // release the LED-Port
		releaseCtrlPort(); // release the control port
		releaseInterruptInPort(); // release the 'interrupt'
		closeI2C(); // close the I2C connection
		closeASC0(); // close the ASC0 connection
		closeASC1(); // close the ASC1 connection
	}
	
	
	private static void closeConnection(ATCommand atCommand) throws Exception {
		try {
			if (null != atCommand) {
				atCommand.release();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private static String readStringFromInputStream(InputStream is) throws Exception {
		String stringBuilder = null;
		try {
			int b;
			stringBuilder = "";
			while ( (b = is.read()) != -1 ) {
				stringBuilder = stringBuilder + new String(new byte[]{(byte) b});
			}			
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return stringBuilder;
	}
	
	private static void writeStringToOutputStream(String data, OutputStream os) throws Exception {
		try {
			byte[] dataBytes = data.getBytes();
			os.write(dataBytes);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}
}
