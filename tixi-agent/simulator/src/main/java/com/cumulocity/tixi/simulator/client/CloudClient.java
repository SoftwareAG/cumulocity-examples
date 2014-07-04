package com.cumulocity.tixi.simulator.client;

import static com.cumulocity.tixi.simulator.model.TixiCredentials.DEVICE_SERIAL;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.tixi.simulator.model.ResponseHandlerFactory;
import com.cumulocity.tixi.simulator.model.TixiCredentials;
import com.cumulocity.tixi.simulator.model.TixiResponse;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

public class CloudClient {

    private static final Logger LOG = LoggerFactory.getLogger(CloudClient.class);

    private String baseUrl;

    private TixiCredentials credentials;

    private ResponseHandlerFactory responseHandlerFactory = new ResponseHandlerFactory();

    private Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class).register(MultiPartFeature.class)
            .register(SseFeature.class);

    public void sendBootstrapRequest() {
        Response response = client.target(baseUrl + "/Tixi/register?serial=" + DEVICE_SERIAL).request().get();
        credentials = response.readEntity(TixiCredentials.class);
    }

    public void sendOpenChannel() {
        WebTarget target = client.target(baseUrl
                + String.format("/Tixi/openchannel?serial={serial}&deviceID={deviceID}&user={user}&password={password}", Map.class,
                        DEVICE_SERIAL, credentials.deviceID, credentials.user, credentials.password));
        EventSource eventSource = new EventSource(target) {
            @Override
            public void onEvent(InboundEvent inboundEvent) {
                TixiResponse response = inboundEvent.readData(TixiResponse.class);
                responseHandlerFactory.getHandler(response).handle(CloudClient.this, response);
            }
        };
        //        eventSource.close();
    }

    public void postExternalDatabaseData(TixiResponse response) {
        sendMultipartRequest(response, "external_database.xml");
    }

    public void postLogDefinitionData(TixiResponse response) {
        sendMultipartRequest(response, "log_definition.xml");
    }

    public void postLogFileData() {
        sendMultipartRequest("sample_data.xml");
    }

    private void sendMultipartRequest(String filename) {
        String requestUrl = baseUrl
                + String.format("/Tixi/senddata?serial={serial}&deviceID={deviceID}&user={user}&password={password}", DEVICE_SERIAL,
                        credentials.deviceID, credentials.user, credentials.password);
        sendMultipartRequest(requestUrl, filename);

    }

    private void sendMultipartRequest(TixiResponse response, String filename) {
        String requestUrl = baseUrl
                + String.format("/Tixi/senddata?serial={serial}&deviceID={deviceID}&user={user}&password={password}&requestId={requestId}",
                        DEVICE_SERIAL, credentials.deviceID, credentials.user, credentials.password, response.getRequestId());
        sendMultipartRequest(requestUrl, filename);
    }

    private void sendMultipartRequest(String requestUrl, String filename) {
        FormDataMultiPart multipart = null;
        try {
            multipart = new FormDataMultiPart();
            FileDataBodyPart filePart = new FileDataBodyPart("filename", new File(this.getClass().getResource("/requests/" + filename)
                    .getFile()));
            MultiPart bodyPart = multipart.bodyPart(filePart);
            WebTarget target = client.target(requestUrl);
            target.request().post(Entity.entity(bodyPart, bodyPart.getMediaType()));
        } finally {
            if (multipart != null) {
                try {
                    multipart.close();
                } catch (IOException e) {
                    LOG.error("", e);
                }
            }
        }
    }
}
