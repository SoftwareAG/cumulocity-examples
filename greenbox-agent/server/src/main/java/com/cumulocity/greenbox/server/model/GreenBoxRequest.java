package com.cumulocity.greenbox.server.model;

import com.cumulocity.greenbox.server.model.json.GreenBoxRequestTypeResolver;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

@JsonTypeInfo(use=JsonTypeInfo.Id.CUSTOM, include=JsonTypeInfo.As.PROPERTY, property="cmd")
@JsonTypeIdResolver(GreenBoxRequestTypeResolver.class)
public abstract class GreenBoxRequest {

    private String hubUID;

    @JsonProperty("cmd")
    private CommandType type;

    private String hub;

    public String getHubUID() {
        return hubUID;
    }

    public void setHubUID(String hubUID) {
        this.hubUID = hubUID;
    }

    public CommandType getType() {
        return type;
    }

    public void setType(CommandType type) {
        this.type = type;
    }

    public String getHub() {
        return hub;
    }

    public void setHub(String hub) {
        this.hub = hub;
    }

}
