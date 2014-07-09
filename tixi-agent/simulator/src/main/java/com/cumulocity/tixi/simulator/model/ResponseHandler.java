package com.cumulocity.tixi.simulator.model;

import com.cumulocity.tixi.simulator.client.CloudClient;

public abstract class ResponseHandler {
    
    public abstract boolean supports(TixiResponse response);

    public abstract void handle(CloudClient client, TixiResponse response);

}
