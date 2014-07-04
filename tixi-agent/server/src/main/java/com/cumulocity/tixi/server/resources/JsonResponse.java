package com.cumulocity.tixi.server.resources;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;

public class JsonResponse {

    @JsonInclude(NON_NULL)
    private String request;

    private Map<String, Object> properties = new HashMap<String, Object>();

    public JsonResponse() {
    }

    public JsonResponse(String request) {
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
    public JsonResponse set(String key, Object value) {
        properties.put(key, value);
        return this;
    }
}
