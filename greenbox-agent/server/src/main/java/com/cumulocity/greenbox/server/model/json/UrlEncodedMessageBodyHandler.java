package com.cumulocity.greenbox.server.model.json;

import static com.google.common.collect.Iterables.getFirst;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.message.internal.FormMultivaluedMapProvider;

import com.cumulocity.greenbox.server.model.GreenBoxRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;

@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Provider
public class UrlEncodedMessageBodyHandler implements MessageBodyReader<GreenBoxRequest> {

    private final FormMultivaluedMapProvider formHandler = new FormMultivaluedMapProvider();

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return GreenBoxRequest.class.isAssignableFrom(type);
    }

    @Override
    public GreenBoxRequest readFrom(Class<GreenBoxRequest> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        final MultivaluedMap<String, String> input = formHandler.readFrom(new MultivaluedHashMap<String, String>(), mediaType, true,
                entityStream);
        final List<String> data = input.get("data");
        return readRequest((data == null || data.isEmpty()) ? "" : getFirst(data, null));
    }

    protected GreenBoxRequest readRequest(String data) throws IOException, JsonParseException, JsonMappingException {
        return mapper.readValue(data, GreenBoxRequest.class);
    }

}
