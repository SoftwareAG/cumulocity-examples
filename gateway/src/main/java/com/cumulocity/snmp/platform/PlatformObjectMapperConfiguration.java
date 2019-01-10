package com.cumulocity.snmp.platform;

import com.cumulocity.model.JSONBase;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import java.io.IOException;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public class PlatformObjectMapperConfiguration {

    public static ObjectMapper configureObjectMapper(ObjectMapper objectMapper) {
        objectMapper.setSerializationInclusion(NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.registerModule(gidModule());
        objectMapper.registerModule(managedObjectModule());
        objectMapper.registerModule(new JodaModule());
        return objectMapper;
    }

    public static SimpleModule gidModule() {
        return new SimpleModule() {{
            addSerializer(GId.class, new JsonSerializer<GId>() {
                @Override
                public void serialize(GId gid, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                    jsonGenerator.writeString(GId.asString(gid));
                }
            });

            addDeserializer(GId.class, new JsonDeserializer<GId>() {
                @Override
                public GId deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
                    return GId.asGId(jsonParser.getValueAsString());
                }
            });
        }};
    }

    public static SimpleModule managedObjectModule() {
        return new SimpleModule() {{
            addDeserializer(ManagedObjectRepresentation.class, new JsonDeserializer<ManagedObjectRepresentation>() {
                @Override
                public ManagedObjectRepresentation deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
                    final ObjectMapper mapper = (ObjectMapper) p.getCodec();
                    final ObjectNode root = mapper.readTree(p);
                    final String json = root.toString();
                    return JSONBase.fromJSON(json, ManagedObjectRepresentation.class);
                }
            });
        }};
    }
}
