package c8y.trackeragent;




import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.SDKException;

public class GL200FuelReport extends GL200Parser implements Translator {

	
	 private TrackerAgent trackerAgent;
	 private String password;
	
	 public static final String FUEL_REPORT = "+RESP:GTFRI";
	 public static final String FUEL_TEMPLATE = "AT+GTCFG=%s,,,,,,,,,,%d,,,,,,,,,,,%04x$";
	 
	 private Logger logger = LoggerFactory.getLogger(getClass());
	 
	 public GL200FuelReport(TrackerAgent trackerAgent, String password) {
	        this.trackerAgent = trackerAgent;
	        this.password = password;
	    }

	
	@Override
	public boolean onParsed(String[] report, String imei) throws SDKException {
		String reportType = report[0];
        if (FUEL_REPORT.equals(reportType)) {
            return Report(report, imei);
        } else {
            return false;
        }
	}

	private boolean Report(String[] report, String imei)
	{
		
		TrackerDevice device = trackerAgent.getOrCreateTrackerDevice(imei);
		
		int pos = 27;
		String instFuelCons =  report[pos];
		//String instFuelCons = "22.0"; 
		logger.debug("InstantFuelConsumption %s",instFuelCons );
		
		try {
			Double instFuelConstDouble= new Double(instFuelCons);
			device.setFuelConsumption(new BigDecimal(instFuelConstDouble), null);
			
	    } catch (NumberFormatException e) {
	    	logger.debug("InstantFuelConsumption is not a Number: %s",instFuelCons );
			
		   return false;
		}
		return true;
	}

	@Override
	public String translate(OperationRepresentation operation) {
		//TODO Implement Operations
		//TODO richtig Implementieren.
		return String.format(FUEL_TEMPLATE, password);
	}

}
