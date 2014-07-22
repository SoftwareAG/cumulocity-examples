package com.cumulocity.tixi.simulator.model;

import com.cumulocity.tixi.simulator.client.CloudClient;



public class StatusResponseHandler extends ResponseHandler {
    
    @Override
    public boolean supports(TixiResponse response) {
        return response.getStatus() != null;
    }

    @Override
    public void handle(CloudClient client, TixiResponse response) {
        if (!response.getStatus().equals("0")) {
            throw new InvalidResponseException();
        }
    }

}
