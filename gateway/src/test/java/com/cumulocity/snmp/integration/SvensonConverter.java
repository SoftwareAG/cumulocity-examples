package com.cumulocity.snmp.integration;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.svenson.tokenize.InputStreamSource;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import static com.cumulocity.model.JSONBase.getJSONGenerator;
import static com.cumulocity.model.JSONBase.getJSONParser;
import static java.nio.charset.Charset.forName;

public class SvensonConverter extends AbstractHttpMessageConverter<Object> {

    public static final Charset DEFAULT_CHARSET = forName("UTF-8");

    public SvensonConverter() {
        super(new MediaType("application", "json", DEFAULT_CHARSET),
                new MediaType("application", "*+json", DEFAULT_CHARSET));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        InputStreamSource inputStreamSource = new InputStreamSource(inputMessage.getBody(), true);
        return getJSONParser().parse(clazz, inputStreamSource);
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try (OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody())) {
            getJSONGenerator().writeJSONToWriter(object, writer);
        }
    }

}