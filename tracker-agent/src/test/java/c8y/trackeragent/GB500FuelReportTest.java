package c8y.trackeragent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;




import c8y.trackeragent.model.FuelMeasurement;

import com.cumulocity.sdk.client.SDKException;

public class GB500FuelReportTest {
	
	public static final String IMEI = "864251020004036";
	//public static final String PASSWORD = "GV500";
	public static final FuelMeasurement fm = new FuelMeasurement();
	
	 public static final String GV500FRISTR = "+RESP:GTFRI,1F0104,864251020004036,,,,10,1,0,,,,0,0,,0262,0007,18d8,6141,00,0.0,,,,77,420000,1829,6,,20110101180334,001B";
	 public static final String GV500FRISTR2 = "+RESP:GTFRI,1F0104,864251020004036,,,,10,1,0,,,,0,0,,0262,0007,18d8,6141,00,0.0,,,,77,420000,832,Inf.,,20110101180334,001B";

	 private TrackerAgent trackerAgent = mock(TrackerAgent.class);
	 private TrackerDevice device = mock(TrackerDevice.class);
	 private GL200FuelReport fuelReport = new GL200FuelReport(trackerAgent);
	
	 @Before
	    public void setup() throws SDKException {
	        when(trackerAgent.getOrCreateTrackerDevice(anyString())).thenReturn(device);
	        
	        fm.setInstantFuelConsumption(new BigDecimal(new Double("6")));
	        
	    }

	 @Test
	    public void gl500FRI() throws SDKException {
		 	String [] resp = GV500FRISTR.split(GL200Constants.FIELD_SEP);
	        String imei = fuelReport.parse(resp);
	        fuelReport.onParsed(resp, imei);

	        assertEquals(IMEI, imei);
	        verify(trackerAgent).getOrCreateTrackerDevice(IMEI);

	        verify(device).setFuelConsumption(new BigDecimal(new Double("6")),null);
	        
	        
	    }
	 @Test
	    public void gl500FRI2() throws SDKException {
		    String [] resp = GV500FRISTR2.split(GL200Constants.FIELD_SEP);
	        String imei = fuelReport.parse(resp);
	        fuelReport.onParsed(resp, imei);

	        assertEquals(IMEI, imei);
	        verify(trackerAgent).getOrCreateTrackerDevice(IMEI);


	        verify(device, never()).setFuelConsumption(new BigDecimal(new Double("6")),null);
	    }
	 
	 
	 
}
