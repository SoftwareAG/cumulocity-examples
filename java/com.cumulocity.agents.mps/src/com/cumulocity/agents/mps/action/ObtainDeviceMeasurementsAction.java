package com.cumulocity.agents.mps.action;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.cumulocity.agents.mps.model.MpsAgent;
import com.cumulocity.agents.mps.model.MpsDevice;
import com.cumulocity.model.energy.measurement.EnergyValue;
import com.cumulocity.model.energy.measurement.ThreePhaseEnergyMeasurement;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.agent.action.AbstractObtainDeviceMeasurementsAction;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.ApacheHttpClient;

/**
 * Obtains measurements from devices.
 */
public class ObtainDeviceMeasurementsAction extends AbstractObtainDeviceMeasurementsAction<MpsDevice> {

	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final String UNIT_KWH = "kWh";

    private static final String UNIT_KVARH = "kVArh";

    private static final String JSON_METER_DATE_TIME = "meterDateTime";

    private static final String JSON_PHASE_3 = "3";

    private static final String JSON_PHASE_2 = "2";

    private static final String JSON_PHASE_1 = "1";

    private static final String JSON_TOTAL = "total";

    private static final String JSON_CAPACITIVE = "capacitive";

    private static final String JSON_INDUCTIVE = "inductive";

    private static final String JSON_ACTIVE = "active";

    private static final String JSON_NEGATIVE = "negative";

    private static final String JSON_POSITIVE = "positive";

    private static final String JSON_REACTIVE = "reactive";

    private static final String JSON_RESULT = "result";

	@Autowired
	public ObtainDeviceMeasurementsAction(MpsAgent agent) {
		super(agent);
	}
	
	@Override
	protected List<MeasurementRepresentation> loadMeasuremntsFromDevice(MpsDevice device) {
		ClientResponse response = getWebResource(device.getMeasurementsUrl()).get(ClientResponse.class);
		try {
			return parseMeasurementClientResponse(device.getGlobalId(), response);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
    protected WebResource getWebResource(String url) {
        Client client = ApacheHttpClient.create();
        client.setFollowRedirects(true);
        WebResource webResource = client.resource(url);
        return webResource;
    }
	
	/**
	 * Parses the {@link ClientResponse} into list of {@link MeasurementRepresentation}'s.
	 * @param gId the device global ID.
	 * @param response the response to parse.
	 * @return parsed measurements.
	 * @throws Exception in case of parsing error.
	 */
	protected List<MeasurementRepresentation> parseMeasurementClientResponse(GId gId, ClientResponse response) throws Exception {		
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.getEntityInputStream());
        Iterator<JsonNode> it = rootNode.get(JSON_RESULT).getElements();
        
        List<MeasurementRepresentation> list = new LinkedList<MeasurementRepresentation>();
        
        while (it.hasNext()) {
            JsonNode elem = it.next();
            MeasurementRepresentation representation = parseMeasurementRepresentation(gId, elem);
            list.add(representation);
        }
		
		return list;
	}
    
	/**
	 * parses single JSON node into {@link MeasurementRepresentation}.
	 * @param gId the device global ID.
	 * @param elem the node to parse.
	 * @return the {@link MeasurementRepresentation} of the node.
	 * @throws ParseException in case of parsing error.
	 */
    private MeasurementRepresentation parseMeasurementRepresentation(GId gId, JsonNode elem) throws ParseException {
        ThreePhaseEnergyMeasurement measurement = parseMeasurement(elem);
        
        String timeString = getTextValue(elem, JSON_METER_DATE_TIME);
        SimpleDateFormat fmt = new SimpleDateFormat(DATE_TIME_FORMAT);
        Date dateTime = fmt.parse(timeString);
        
        MeasurementRepresentation representation = new MeasurementRepresentation();
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(gId);
        representation.setSource(source);
        representation.setTime(dateTime);
        representation.setType("MPSDeviceMeter");
        representation.set(measurement);
        return representation;
    }
    
    /**
     * Parses single JSON node into {@link ThreePhaseEnergyMeasurement}.
     * @param elem the node to parse.
     * @return the {@link ThreePhaseEnergyMeasurement} of the node.
     * @throws ParseException in case of parsing error.
     */
    private ThreePhaseEnergyMeasurement parseMeasurement(JsonNode elem) throws ParseException {
        ThreePhaseEnergyMeasurement measurement = new ThreePhaseEnergyMeasurement();
        
        JsonNode reactiveEnergy = getSubNode(elem, JSON_REACTIVE);
        
        String Ri_plus_total = getTextValue(reactiveEnergy, JSON_POSITIVE, JSON_INDUCTIVE, JSON_TOTAL);
        measurement.setTotalReactiveInductiveEnergyIn(createEnergyValue(Ri_plus_total, UNIT_KVARH));
        
        String Rc_plus_total = getTextValue(reactiveEnergy, JSON_POSITIVE, JSON_CAPACITIVE, JSON_TOTAL);
        measurement.setTotalReactiveCapacitiveEnergyIn(createEnergyValue(Rc_plus_total, UNIT_KVARH));
        
        String Ri_minus_total = getTextValue(reactiveEnergy, JSON_NEGATIVE, JSON_INDUCTIVE, JSON_TOTAL);
        measurement.setTotalReactiveInductiveEnergyOut(createEnergyValue(Ri_minus_total, UNIT_KVARH));
        
        String Rc_minus_total = getTextValue(reactiveEnergy, JSON_NEGATIVE, JSON_CAPACITIVE, JSON_TOTAL);
        measurement.setTotalReactiveCapacitiveEnergyOut(createEnergyValue(Rc_minus_total, UNIT_KVARH));
        

        JsonNode activePositiveEnergy = getSubNode(elem, JSON_ACTIVE, JSON_POSITIVE);
        
        String A_plus_total = getTextValue(activePositiveEnergy, JSON_TOTAL);
        measurement.setTotalActiveEnergyIn(createEnergyValue(A_plus_total, UNIT_KWH));
        
        String A_plus_phase1 = getTextValue(activePositiveEnergy, JSON_PHASE_1);
        measurement.setTotalActiveEnergyInPhase1(createEnergyValue(A_plus_phase1, UNIT_KWH));
        
        String A_plus_phase2 = getTextValue(activePositiveEnergy, JSON_PHASE_2);
        measurement.setTotalActiveEnergyInPhase2(createEnergyValue(A_plus_phase2, UNIT_KWH));
        
        String A_plus_phase3 = getTextValue(activePositiveEnergy, JSON_PHASE_3);
        measurement.setTotalActiveEnergyInPhase3(createEnergyValue(A_plus_phase3, UNIT_KWH));
        
        
        JsonNode activeNegativeEnergy = getSubNode(elem, JSON_ACTIVE, JSON_NEGATIVE);
        
        String A_minus_total = getTextValue(activeNegativeEnergy, JSON_TOTAL);
        measurement.setTotalActiveEnergyOut(createEnergyValue(A_minus_total, UNIT_KWH));
        
        String A_minus_phase1 = getTextValue(activeNegativeEnergy, JSON_PHASE_1);
        measurement.setTotalActiveEnergyOutPhase1(createEnergyValue(A_minus_phase1, UNIT_KWH));
        
        String A_minus_phase2 = getTextValue(activeNegativeEnergy, JSON_PHASE_2);
        measurement.setTotalActiveEnergyOutPhase2(createEnergyValue(A_minus_phase2, UNIT_KWH));
        
        String A_minus_phase3 = getTextValue(activeNegativeEnergy, JSON_PHASE_3);
        measurement.setTotalActiveEnergyOutPhase3(createEnergyValue(A_minus_phase3, UNIT_KWH));
        
        return measurement;
    }
    
    /**
     * Gets the sub-node for given path.
     * @param elem the node to start looking from.
     * @param path the path to traverse in search of target node.
     * @return the target node from the end of the path, or <code>null</code> if it does not exist.
     */
    private JsonNode getSubNode(JsonNode elem, String...path) {
    	if (elem == null) {
    		return null;
    	}
    	if (path == null || path.length == 0) {
    		return elem;
    	}
    	
    	JsonNode nextElem = elem.get(path[0]);
    	int nextLen = path.length - 1;
    	
    	if (nextLen == 0) {
    		return getSubNode(nextElem);
    	}
    	
    	String[] nextPath = new String[nextLen];
    	System.arraycopy(path, 1, nextPath, 0, nextLen);
    	return getSubNode(nextElem, nextPath);
    }
    
    /**
     * Gets the text value from the end of  given path.
     * @param elem the node to start looking from.
     * @param path the path to traverse in search of target node.
     * @return the text value of target node, or <code>null</code> if it does not exist.
     * @see #getSubNode(JsonNode, String...)
     */
    private String getTextValue(JsonNode elem, String...path) {
    	JsonNode subNode = getSubNode(elem, path);
    	return subNode == null ? null : subNode.getValueAsText();
    }
    
    /**
     * Creates {@link EnergyValue} of given value and unit. 
     * @param value the value of energy.
     * @param unit the unit of value.
     * @return the {@link EnergyValue}, or <code>null</code> if the value passed was <code>null</code>.
     */
    private EnergyValue createEnergyValue(String value, String unit) {
    	if (value == null) {
    		return null;
    	}
        EnergyValue ev = new EnergyValue();
        ev.setUnit(unit);
        ev.setValue(new BigDecimal(value));
        return ev;
    }
}
