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

package c8y.tinkerforge.bricklet;

import java.math.BigDecimal;
import java.util.Properties;

import c8y.TemperatureMeasurement;
import c8y.TemperatureSensor;
import c8y.tinkerforge.TFIds;

import com.tinkerforge.BrickletPTC;
import com.tinkerforge.Device;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class PTCBricklet extends BaseSensorBricklet {

	private static final String TYPE="Temperature";
	private static final short DEFAULT_WIRE_MODE=BrickletPTC.WIRE_MODE_2;
	private static final String WIRE_MODE_PROP=".wiremode";
	
	private String wireModeProp;
	private short wireMode;
	
	private TemperatureMeasurement temperature = new TemperatureMeasurement();
	
	
	public PTCBricklet(String id, Device device) {
		super(id, device, TYPE, new TemperatureSensor());
		wireModeProp=TFIds.getPropertyName(TYPE)+WIRE_MODE_PROP;
		wireMode=DEFAULT_WIRE_MODE;
	}
	
	@Override
	public void addDefaults(Properties props) {
		super.addDefaults(props);
		props.setProperty(TFIds.getPropertyName(TYPE) + WIRE_MODE_PROP, Short.toString(DEFAULT_WIRE_MODE));
	}

	@Override
	public void configurationChanged(Properties props) {
		super.configurationChanged(props);
		try {
            String wireModeStr = props.getProperty(wireModeProp, Short.toString(DEFAULT_WIRE_MODE));
            changeWireMode(Short.parseShort(wireModeStr));
        } catch (NumberFormatException x) {
            logger.warn("Wire mode format issue", x);
        } catch (TimeoutException | NotConnectedException x) {
        	logger.warn("Error setting wire mode", x);
        } 
	}
	
	private void changeWireMode(short newWireMode) throws TimeoutException, NotConnectedException{
		if(this.wireMode!=newWireMode){
			if(newWireMode>=2 && newWireMode<=4){
				this.wireMode=newWireMode;
				((BrickletPTC)getDevice()).setWireMode(newWireMode);
			} else throw new IllegalStateException("Wrong value for property "+wireModeProp+" = "+newWireMode);
		}
	}
	
	@Override
	public void initialize() throws Exception {
		// Nothing to be done.
	}

	@Override
	public void run() {
		try {
			BrickletPTC ptc=(BrickletPTC)getDevice();
			temperature.setTemperature(new BigDecimal((double)ptc.getTemperature()/100));
			super.sendMeasurement(temperature);
		} catch(Exception e) {
			logger.warn("Cannot read temperature from PTC bricklet", e);
		}
	}

}
