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
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

/**
 * Performs the communication with a connected device. Accepts reports from the
 * input stream and sends commands to the output stream. 
 */
public class ConnectedTracker implements Runnable, Executor {
	public ConnectedTracker(Socket client, List<Object> fragments,
			char reportSeparator, String fieldSeparator) {
		this.client = client;
		this.fragments = fragments;
		this.reportSeparator = reportSeparator;
		this.fieldSeparator = fieldSeparator;
	}

	@Override
	public void run() {
		try (InputStream is = client.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				OutputStream out = client.getOutputStream()) {
			setOut(out);
			processReports(bis);
		} catch (IOException e) {
			logger.warn("Error during communication with client device", e);
		} catch (SDKException e) {
			logger.warn("Error during communication with the platform", e);
		}
	}

	void processReports(InputStream is) throws IOException, SDKException {
		String reportStr;
		while ((reportStr = readReport(is)) != null) {
			logger.debug("Processing report: " + reportStr);
			String[] report = reportStr.split(fieldSeparator);
			processReport(report);
		}
		logger.debug("Connection closed by {} {} ", client.getRemoteSocketAddress(), imei);
	}

	String readReport(InputStream is) throws IOException {
		StringBuffer result = new StringBuffer();
		int c;

		while ((c = is.read()) != -1) {
			if ((char) c == reportSeparator) {
				break;
			}
			if ((char) c == '\n') {
				continue;
			}
			result.append((char) c);
		}

		if (c == -1) {
			return null;
		}

		return result.toString();
	}

	void processReport(String[] report) throws SDKException {
		for (Object fragment : fragments) {
			if (fragment instanceof Parser) {
				Parser parser = (Parser) fragment;
				String imei = parser.parse(report);
				if (imei != null) {
					this.imei = imei;
					if (!ConnectionRegistry.instance().containsKey(imei)) {
						// Works because no two devices have the same IMEI.
						ConnectionRegistry.instance().put(imei, this);
					}

					break;
				}				
			}
		}
	}

	@Override
	public void execute(OperationRepresentation operation) throws IOException {
		logger.debug("Executing operation " + operation);
		String translation = translate(operation);

		if (translation != null) {
			out.write(translation.getBytes());
			out.flush();
		} else {
			operation.setStatus(OperationStatus.FAILED.toString());
			operation.setFailureReason("Command currently not supported");
		}
	}

	public String translate(OperationRepresentation operation) {
		for (Object fragment : fragments) {
			if (fragment instanceof Translator) {
				Translator translator = (Translator) fragment;
				String translation = translator.translate(operation);
				if (translation != null) {
					return translation;
				}
			}
		}
		return null;
	}
	
	void setOut(OutputStream out) {
		this.out = out;
	}

	protected Logger logger = LoggerFactory.getLogger(ConnectedTracker.class);
	private char reportSeparator;
	private String fieldSeparator;

	private Socket client;
	private List<Object> fragments = new ArrayList<Object>();
	private OutputStream out;
	private String imei;
}
