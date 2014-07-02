package com.cumulocity.tixi.simulator.model;

import com.cumulocity.tixi.simulator.client.CloudClient;


public class HeartBeatResponseHandler extends ResponseHandler {
    
    @Override
    public boolean supports(TixiResponse response) {
        String request = response.getRequest();
        if (request == null) {
            return false;
        }
        return request.equals("IDLE");
    }

    @Override
    public void handle(CloudClient client, TixiResponse response) {
        client.sendHeartbeat(response);
    }

}
