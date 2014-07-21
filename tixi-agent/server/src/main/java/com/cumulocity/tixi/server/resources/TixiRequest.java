package com.cumulocity.tixi.server.resources;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;

public class TixiRequest {

    public static final Long STATUS_OK = 0L;
    public static final Long STATUS_KO = -1L;

	@JsonInclude(NON_NULL)
    private String request;

    private Map<String, Object> properties = new HashMap<String, Object>();

    public TixiRequest() {
    }

    public TixiRequest(String request) {
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
    public TixiRequest set(String key, Object value) {
        properties.put(key, value);
        return this;
    }
    
    public static TixiRequest statusOK() {
        return new TixiRequest().set("status", STATUS_OK);
    }
    
    public static TixiRequest statusKO() {
    	return new TixiRequest().set("status", STATUS_KO);
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
	    TixiRequest other = (TixiRequest) obj;
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
