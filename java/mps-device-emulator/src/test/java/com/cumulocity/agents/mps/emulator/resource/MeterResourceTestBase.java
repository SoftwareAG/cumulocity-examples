package com.cumulocity.agents.mps.emulator.resource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.svenson.JSONParser;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class MeterResourceTestBase {
    
    private Client client;

    private WebResource resource;

    protected Map<String, Object> issueGet(String path) throws Exception {
        ClientResponse clientResponse = getResource().path(path).get(ClientResponse.class);
        return convertOkResponse(clientResponse);
    }

    protected Map<String, Object> issuePost(String path) throws Exception {
        ClientResponse clientResponse = getResource().path(path).post(ClientResponse.class);
        return convertOkResponse(clientResponse);
    }
    
    private Map<String, Object> convertOkResponse(ClientResponse clientResponse) {
        @SuppressWarnings("unchecked")
        Map<String, Object> response = JSONParser.defaultJSONParser().parse(Map.class, clientResponse.getEntity(String.class));
        assertThat(clientResponse.getStatus(), is(equalTo(ClientResponse.Status.OK.getStatusCode())));
        return response;
    }
    
    protected WebResource getResource() throws Exception {
        String url = getServerURL();
        ClientConfig cc = new DefaultClientConfig();
        client = Client.create(cc);
        if (resource == null) {
            resource = client.resource(url);
        }
        return resource;
    }

    private String getServerURL() throws Exception {
        URL resource = getClass().getClassLoader().getResource("META-INF/spring/cumulocity-core.properties");
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(resource.toURI())));
        return properties.getProperty("http.server") + "/";
    }
}
