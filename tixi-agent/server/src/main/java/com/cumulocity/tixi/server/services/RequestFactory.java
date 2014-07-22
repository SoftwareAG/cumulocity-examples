package com.cumulocity.tixi.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cumulocity.tixi.server.model.RequestType;
import com.cumulocity.tixi.server.model.txml.LogDefinition;
import com.cumulocity.tixi.server.request.util.RequestIdFactory;
import com.cumulocity.tixi.server.request.util.RequestStorage;
import com.cumulocity.tixi.server.resources.TixiJsonResponse;

@Component
public class RequestFactory {

    private final RequestIdFactory requestIdFactory;
    
    private final RequestStorage requestStorage;
    
    @Autowired
    public RequestFactory(RequestIdFactory requestIdFactory, RequestStorage requestStorage) {
        this.requestIdFactory = requestIdFactory;
        this.requestStorage = requestStorage;
    }
    
    public TixiJsonResponse create(RequestType requestType) {
        if (requestType == RequestType.EXTERNAL_DATABASE) {
            return createExternalDBRequest();
        }
        if (requestType == RequestType.LOG_DEFINITION) {
            return createLogDefinitionRequest();
        }
        throw new RuntimeException("Unknown request type");
    }
    
    private TixiJsonResponse createLogDefinitionRequest() {
        String requestId = requestIdFactory.get().toString();
        requestStorage.put(requestId, LogDefinition.class);
        return TixiJsonResponse.createLogDefinitionRequest(requestId);
    }

    private TixiJsonResponse createExternalDBRequest() {
        String requestId = requestIdFactory.get().toString();
        return TixiJsonResponse.createExternalDBRequest(requestId);
    }

}
