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
/*
 * Copyright 2012 Nokia Siemens Networks 
 */
package com.cumulocity.agents.mps.emulator.resource;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.svenson.JSON;

import com.cumulocity.agents.mps.emulator.config.JAXRSResource;
import com.cumulocity.agents.mps.emulator.model.MeterList;
import com.cumulocity.agents.mps.emulator.model.MeterProfile;
import com.cumulocity.agents.mps.emulator.model.MeterProperties;
import com.cumulocity.agents.mps.emulator.model.MeterReadOut;
import com.cumulocity.agents.mps.emulator.model.MeterState;
import com.cumulocity.agents.mps.emulator.utils.DateParser;

/**
 * The examlpe REST resource. Be sure to mark all REST resources with {@link JAXRSResource}
 * to allow their auto-discovery and registration within JAX-RS server.
 * @author Darek Kaczynski
 */
@JAXRSResource
@Path(MeterResource.METER_PATH_TEMPLATE)
public class MeterResource {

    private static final Logger LOG = LoggerFactory.getLogger(MeterResource.class);

    public static final String CHANGE_STATE_PATH_TEMPLATE = "relay/{id}/{state}";

    public static final String GET_STATE_PATH_TEMPLATE = "relay/{id}/GET";

    public static final String READOUT_PATH_TEMPLATE = "/readout/{id}/{country}";

    public static final String PROFILE_PATH_TEMPLATE = "/profile/{id}/{start}/{end}";

    public static final String LIST_PATH_TEMPLATE = "/list/";

    public static final String PROPERTIES_PATH_TEMPLATE = "/properties/{id}";

    public static final String METER_PATH_TEMPLATE = "/meter";

    public static final String STATE_PARAM = "state";

    public static final String ID_PARAM = "id";

    public static final String COUNTRY_PARAM = "country";

    public static final String START_PARAM = "start";

    public static final String END_PARAM = "end";

    private static final MeterState CHANGE_STATE_RESPONSE = new MeterState(true, "True");

    private static final JSON jsonGenerator = new JSON();

    private String state = "OFF";
    

    @GET
    @Path(READOUT_PATH_TEMPLATE)
    @Produces("application/json")
    public String getReadOut(@PathParam(ID_PARAM) String id, @PathParam(COUNTRY_PARAM) String country) {
        LOG.info("gathering measurements for meter " + id);
        return jsonGenerator.forValue(MeterReadOut.createReading());
    }

    @GET
    @Path(PROFILE_PATH_TEMPLATE)
    @Produces("application/json")
    public String getProfile(@PathParam(ID_PARAM) String id, @PathParam(START_PARAM) String start, @PathParam(END_PARAM) String end) {
        Date from = DateParser.parseDate(start);
        Date to = DateParser.parseDate(end);
        return jsonGenerator.forValue(MeterProfile.createSampleMeterProfile(from, to));
    }

    @GET
    @Path(LIST_PATH_TEMPLATE)
    @Produces("application/json")
    public String getList() {
        LOG.info("sending list of meters");
        return jsonGenerator.forValue(MeterList.SAMPLE_LIST);
    }

    @GET
    @Path(PROPERTIES_PATH_TEMPLATE)
    @Produces("application/json")
    public String getProperties(@PathParam(ID_PARAM) String id) {
        return jsonGenerator.forValue(MeterProperties.SAMPLE_PROPERTIES);
    }

    @POST
    @Path(CHANGE_STATE_PATH_TEMPLATE)
    @Produces("application/json")
    public Response changeState(@PathParam(ID_PARAM) String id, @PathParam(STATE_PARAM) String state) {
        LOG.info("changing meter relay state to " + state);
        this.state = state;
        return Response.ok(jsonGenerator.forValue(CHANGE_STATE_RESPONSE)).build();
    }

    @POST
    @Path(GET_STATE_PATH_TEMPLATE)
    @Produces("application/json")
    public Response getState(@PathParam(ID_PARAM) String id) {
        LOG.info("retrieving meter state");
        return Response.ok(jsonGenerator.forValue(new MeterState(true, state))).build();
    }
}
