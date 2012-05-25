/*
 * Copyright 2012 Nokia Siemens Networks 
 */
package com.cumulocity.agents.mps.driver;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.svenson.JSONParser;
import org.svenson.tokenize.InputStreamSource;

import com.cumulocity.agents.mps.model.MpsAgent;
import com.cumulocity.agents.mps.model.MpsDevice;
import com.cumulocity.agents.mps.model.MpsRelayEvent;
import com.cumulocity.agents.mps.model.measurement.BipolarElectricMeasurement;
import com.cumulocity.agents.mps.model.measurement.ElectricMeasurement;
import com.cumulocity.agents.mps.model.measurement.MpsMeasurement;
import com.cumulocity.agents.mps.model.measurement.MpsResponse;
import com.cumulocity.agents.mps.model.measurement.converter.MpsTypeConverterRepository;
import com.cumulocity.model.control.Relay;
import com.cumulocity.model.control.Relay.RelayState;
import com.cumulocity.model.energy.measurement.EnergyValue;
import com.cumulocity.model.energy.measurement.ThreePhaseEnergyMeasurement;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.agent.driver.DeviceDriver;
import com.cumulocity.sdk.agent.driver.DeviceException;
import com.cumulocity.sdk.client.Platform;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.ApacheHttpClient;

/**
 * A {@link DeviceDriver} implementation for MPS device.
 */
public class MpsDeviceDriver implements DeviceDriver<MpsDevice> {

	private static final Logger LOG = LoggerFactory.getLogger(MpsDeviceDriver.class);
	
    private static final String UNIT_KWH = "kWh";

    private static final String UNIT_KVARH = "kVArh";
    
    private static final JSONParser JSON_PARSER;
    
    static {
        JSON_PARSER = new JSONParser(JSONParser.defaultJSONParser());
        JSON_PARSER.setTypeConverterRepository(MpsTypeConverterRepository.getInstance());
    }
	
	private Platform platform;
	
	private MpsAgent agent;
	
	@Autowired
	public MpsDeviceDriver(Platform platform, MpsAgent agent) {
        this.platform = platform;
        this.agent = agent;
    }
	
    @Override
    public List<MeasurementRepresentation> loadMeasuremntsFromDevice(MpsDevice device) throws DeviceException {
        ClientResponse response = getWebResource(device.getMeasurementsUrl()).get(ClientResponse.class);
        try {
            return parseMeasurementClientResponse(device.getGlobalId(), response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public boolean isOperationSupported(OperationRepresentation operation) {
        if (operation.get(Relay.class) == null) {
            LOG.warn(String.format("Unknown operation with id: %s.", operation.getId().toJSON()));
            return false;
        }
        
        return true;
    }

    @Override
    public void handleSupportedOperation(OperationRepresentation operation) throws DeviceException {
        Relay relay = operation.get(Relay.class);
        RelayState relayState = relay.getRelayState();
        
        if (relayState == null) {
            throw new DeviceException(String.format("Relay received but no RelayState present. Ignoring. OperationID: %s.", 
                    operation.getId().toJSON()));
        }
        
        MpsDevice device = agent.getDevice(operation.getDeviceId());
        if (device == null){
            throw new DeviceException(String.format("Relay received for unknown meter device GId: %s. OperationID: %s.", 
                    operation.getDeviceId().toJSON(), operation.getId().toString()));
        }
        
        executeRelayOperationOnDevice(device, relayState); 
        
        addEventToPlatform(operation.getDeviceId(), relayState);
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
    protected List<MeasurementRepresentation> parseMeasurementClientResponse(GId gId, ClientResponse response)
            throws Exception {
        
        InputStreamSource source = new InputStreamSource(response.getEntityInputStream(), false);
        MpsResponse mpsResponse = JSON_PARSER.parse(MpsResponse.class, source);
        
        List<MeasurementRepresentation> list = new LinkedList<MeasurementRepresentation>();
        
        for (MpsMeasurement mpsMeasurement : mpsResponse.getResult()) {
            list.add(parseMeasurementRepresentation(gId, mpsMeasurement));
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
    private MeasurementRepresentation parseMeasurementRepresentation(GId gId, MpsMeasurement mpsMeasurement) 
            throws ParseException {
        
        ThreePhaseEnergyMeasurement measurement = parseMeasurement(mpsMeasurement);
        
        MeasurementRepresentation representation = new MeasurementRepresentation();
        ManagedObjectRepresentation source = new ManagedObjectRepresentation();
        source.setId(gId);
        representation.setSource(source);
        representation.setTime(mpsMeasurement.getMeterDateTime());
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
    private ThreePhaseEnergyMeasurement parseMeasurement(MpsMeasurement mpsMeasurement) throws ParseException {
        ThreePhaseEnergyMeasurement measurement = new ThreePhaseEnergyMeasurement();
        
        BipolarElectricMeasurement reactiveEnergy = mpsMeasurement.getReactive();
        
        String Ri_plus_total = reactiveEnergy.getPositive().getInductive().getTotal();
        measurement.setTotalReactiveInductiveEnergyIn(createEnergyValue(Ri_plus_total, UNIT_KVARH));
        
        String Rc_plus_total = reactiveEnergy.getPositive().getCapacitive().getTotal();
        measurement.setTotalReactiveCapacitiveEnergyIn(createEnergyValue(Rc_plus_total, UNIT_KVARH));
        
        String Ri_minus_total = reactiveEnergy.getNegative().getInductive().getTotal();
        measurement.setTotalReactiveInductiveEnergyOut(createEnergyValue(Ri_minus_total, UNIT_KVARH));
        
        String Rc_minus_total = reactiveEnergy.getNegative().getCapacitive().getTotal();
        measurement.setTotalReactiveCapacitiveEnergyOut(createEnergyValue(Rc_minus_total, UNIT_KVARH));
        

        ElectricMeasurement activePositiveEnergy = mpsMeasurement.getActive().getPositive();
        
        String A_plus_total = activePositiveEnergy.getTotal();
        measurement.setTotalActiveEnergyIn(createEnergyValue(A_plus_total, UNIT_KWH));
        
        String A_plus_phase1 = activePositiveEnergy.getPhase1();
        measurement.setTotalActiveEnergyInPhase1(createEnergyValue(A_plus_phase1, UNIT_KWH));
        
        String A_plus_phase2 = activePositiveEnergy.getPhase2();
        measurement.setTotalActiveEnergyInPhase2(createEnergyValue(A_plus_phase2, UNIT_KWH));
        
        String A_plus_phase3 = activePositiveEnergy.getPhase3();
        measurement.setTotalActiveEnergyInPhase3(createEnergyValue(A_plus_phase3, UNIT_KWH));
        
        
        ElectricMeasurement activeNegativeEnergy = mpsMeasurement.getActive().getNegative();
        
        String A_minus_total = activeNegativeEnergy.getTotal();
        measurement.setTotalActiveEnergyOut(createEnergyValue(A_minus_total, UNIT_KWH));
        
        String A_minus_phase1 = activeNegativeEnergy.getPhase1();
        measurement.setTotalActiveEnergyOutPhase1(createEnergyValue(A_minus_phase1, UNIT_KWH));
        
        String A_minus_phase2 = activeNegativeEnergy.getPhase2();
        measurement.setTotalActiveEnergyOutPhase2(createEnergyValue(A_minus_phase2, UNIT_KWH));
        
        String A_minus_phase3 = activeNegativeEnergy.getPhase3();
        measurement.setTotalActiveEnergyOutPhase3(createEnergyValue(A_minus_phase3, UNIT_KWH));
        
        return measurement;
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
	
	/**
	 * Executes the operation on the device.
	 * @param device the device to execute the operation on.
	 * @param relayState the state to set.
	 * @return <code>OperationExecutionResult</code> which corresponds result of execution
	 */
	private void executeRelayOperationOnDevice(MpsDevice device, RelayState relayState) throws DeviceException {
	    
        ClientResponse response = getWebResource(device.getChangeStateUrl(relayState)).post(ClientResponse.class);
        
        if (Status.OK.getStatusCode() != response.getStatus()) {
            throw new DeviceException(String.format("Error setting relay state! Recieved HTTP status %d.",
                    response.getStatus()));        	
        }
        
        LOG.info("Relay state set sucessfully.");
	}
	
	/**
	 * Adds an event about operation executed on the device.
	 * @param deviceId the GID of the device the operation was executed on.
	 * @param relayState the state set to the device.
	 * @return <code>OperationExecutionResult</code> which corresponds result of execution
	 */
	private void addEventToPlatform(GId deviceId, RelayState relayState) throws DeviceException {
        MpsRelayEvent event = new MpsRelayEvent(deviceId, relayState);
        try {
        	platform.getEventApi().create(event);
        } catch (Exception e) {
        	String failureReason = "Problem posting event";
            LOG.error(failureReason, e);
            throw new DeviceException(failureReason, e);
        }
	}
}
