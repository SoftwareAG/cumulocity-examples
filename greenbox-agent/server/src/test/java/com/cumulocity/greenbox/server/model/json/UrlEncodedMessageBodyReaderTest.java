package com.cumulocity.greenbox.server.model.json;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Test;

import com.cumulocity.greenbox.server.model.GreenBoxRequest;
import com.cumulocity.greenbox.server.model.GreenBoxSendRequest;
import com.cumulocity.greenbox.server.model.GreenBoxSetupRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;

public class UrlEncodedMessageBodyReaderTest {

    @Test
    public void shouldDecodeSetupMessage() throws JsonParseException, JsonMappingException, IOException {
        final CharSource source = Resources.asCharSource(Resources.getResource("commands/setup.json"), Charset.forName("utf8"));

        UrlEncodedMessageBodyHandler reader = new UrlEncodedMessageBodyHandler();

        final GreenBoxRequest request = reader.readRequest(source.read());

        assertThat(request).isNotNull().isInstanceOf(GreenBoxSetupRequest.class);

    }
    
    @Test
    public void shouldDecodeSendMessage() throws JsonParseException, JsonMappingException, IOException {
        final CharSource source = Resources.asCharSource(Resources.getResource("commands/send.json"), Charset.forName("utf8"));

        UrlEncodedMessageBodyHandler reader = new UrlEncodedMessageBodyHandler();

        final GreenBoxRequest request = reader.readRequest(source.read());

        assertThat(request).isNotNull().isInstanceOf(GreenBoxSendRequest.class);

    }
}
