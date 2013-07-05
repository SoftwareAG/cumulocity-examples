/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package c8y.trackeragent;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.sdk.client.SDKException;

public class QueclinkTrackerReader implements Runnable {
	public static final char CMD_SEPARATOR = '$';
	public static final String FIELD_SEPARATOR = ",";

	public QueclinkTrackerReader(Socket client, TrackerManager trackerMgr) {
		this.client = client;
		this.trackerMgr = trackerMgr;
	}

	@Override
	public void run() {
		try (InputStream is = client.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is)) {

			String command;	
			while ((command = readCommand(is)) != null) {
				execute(command);
			}			
		} catch (IOException e) {
			logger.warn(
					"Exception caught during communication with client device",
					e);
		} catch (SDKException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
	}

	private String readCommand(InputStream is) throws IOException {
		StringBuffer result = new StringBuffer();
		int c=0;
		
		while ((c = is.read()) != -1) {
			if ((char)c == CMD_SEPARATOR) {
				break;
			}
			if ((char)c == '\n') {
				continue;
			}
			result.append((char)c);
		}
		
		if (c == -1) {
			return null;
		}
		
		return result.toString();
	}
	
	private String execute(String command) throws SDKException {
        logger.debug("Executing " + command);
		String[] parameters = command.split(FIELD_SEPARATOR);
		
		if ("+RESP:GTGEO".equals(parameters[0])) {
		    ReportParameters reportParameters = new ReportParameters(parameters);
	        trackerMgr.locationUpdate(reportParameters.getImei(), new BigDecimal(reportParameters.getLatitude()), new BigDecimal(reportParameters.getLongitude()), new BigDecimal(reportParameters.getAltitude()));
		}

		// Do the processing and invoke tracker mgr 
		// trackerMgr.locationUpdate(imei, latitude, longitude, altitude);

		return null;
	}
	

	private Logger logger = LoggerFactory.getLogger(Agent.class);
	private Socket client;
	private TrackerManager trackerMgr;
}
