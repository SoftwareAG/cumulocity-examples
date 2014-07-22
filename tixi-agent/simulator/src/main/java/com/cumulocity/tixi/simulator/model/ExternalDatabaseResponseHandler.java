package com.cumulocity.tixi.simulator.model;

import com.cumulocity.tixi.simulator.client.CloudClient;


public class ExternalDatabaseResponseHandler extends ResponseHandler {
    
    @Override
    public boolean supports(TixiResponse response) {
        String request = response.getRequest();
        String parameter = response.getParameter();
        
        if (request == null || parameter == null) {
            return false;
        }
        return request.equals("TiXML") && parameter.contains("PROCCFG/External");
    }

    @Override
    public void handle(CloudClient client, TixiResponse response) {
        client.postExternalDatabaseData(response);
    }

}
