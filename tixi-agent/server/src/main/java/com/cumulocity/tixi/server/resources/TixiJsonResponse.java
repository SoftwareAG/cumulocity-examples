package com.cumulocity.tixi.server.resources;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.util.HashMap;
import java.util.Map;

import com.cumulocity.tixi.server.model.txml.LogDefinition;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;

public class TixiJsonResponse {

    public static final Long STATUS_OK = 0l;

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
        return new TixiJsonResponse().set("status", STATUS_OK);
    }
    
    public static TixiJsonResponse createLogDefinitionRequest(String requestId) {
        return new TixiJsonResponse("TiXML").set("requestId", requestId).set("parameter", "[<GetConfig _=\"LOG/LogDefinition\" ver=\"v\"/>]");
    }

    public static  TixiJsonResponse createExternalDBRequest(String requestId) {
        return new TixiJsonResponse("TiXML").set("requestId", requestId).set("parameter",
                "[<GetConfig _=\"PROCCFG/External\" ver=\"v\"/>]");
    }

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((properties == null) ? 0 : properties.hashCode());
	    result = prime * result + ((request == null) ? 0 : request.hashCode());
	    return result;
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    TixiJsonResponse other = (TixiJsonResponse) obj;
	    if (properties == null) {
		    if (other.properties != null)
			    return false;
	    } else if (!properties.equals(other.properties))
		    return false;
	    if (request == null) {
		    if (other.request != null)
			    return false;
	    } else if (!request.equals(other.request))
		    return false;
	    return true;
    }
}
