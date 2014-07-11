package com.cumulocity.tixi.simulator.model;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.tixi.simulator.client.CloudClient;

public class ResponseHandlerFactory {
    
    private static final Logger LOG = LoggerFactory.getLogger(ResponseHandlerFactory.class);
    
    private List<ResponseHandler> handlers = new ArrayList<ResponseHandler>();
    
    public ResponseHandlerFactory() {
        handlers.add(new StatusResponseHandler());
        handlers.add(new ExternalDatabaseResponseHandler());
        handlers.add(new LogDefinitionResponseHandler());
    }

    public ResponseHandler getHandler(TixiResponse response) {
         for (ResponseHandler handler : handlers) {
             if (handler.supports(response)) {
                 return handler;
             }
         }
         return new NullResponseHandler();
    }
    
    public static class NullResponseHandler extends ResponseHandler {

        @Override
        public boolean supports(TixiResponse response) {
            return false;
        }

        @Override
        public void handle(CloudClient client, TixiResponse response) {
            LOG.error("Failed to find handler for the response");
        }
        
    }

}
