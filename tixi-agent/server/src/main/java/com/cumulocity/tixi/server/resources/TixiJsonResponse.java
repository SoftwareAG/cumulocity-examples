package com.cumulocity.tixi.server.resources;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.util.HashMap;
import java.util.Map;

import com.cumulocity.tixi.server.model.txml.LogDefinition;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;

public class TixiJsonResponse {

    @JsonInclude(NON_NULL)
    private String request;

    private Map<String, Object> properties = new HashMap<String, Object>();

    public TixiJsonResponse() {
    }

    public TixiJsonResponse(String request) {
        this.request = request;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return properties;
    }

    @JsonAnySetter
    public TixiJsonResponse set(String key, Object value) {
        properties.put(key, value);
        return this;
    }
    
    public static TixiJsonResponse statusOKJson() {
        return new TixiJsonResponse().set("status", 0l);
    }
    
    public static TixiJsonResponse createLogDefinitionRequest(String requestId) {
        return new TixiJsonResponse("TiXML").set("requestId", requestId).set("parameter", "[<GetConfig _=\"LOG/LogDefinition\" ver=\"v\"/>]");
    }

    public static  TixiJsonResponse createExternalDBRequest(String requestId) {
        return new TixiJsonResponse("TiXML").set("requestId", requestId).set("parameter",
                "[<GetConfig _=\"PROCCFG/External\" ver=\"v\"/>]");
    }
}
