package com.cumulocity.snmp.persistance.repository;

import com.cumulocity.snmp.model.core.HasKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
class PersistableJacksonSerializer<T extends HasKey> extends Serializer<T> {

    private final ObjectMapper objectMapper;
    private final PersistableTypeMetadataProvider metadataProvider;

    @Override
    public void serialize(final DataOutput out, final T value) throws IOException {
        out.writeUTF(metadataProvider.getTypeName(value.getClass()));
        out.writeUTF(metadataProvider.getDiscriminator(value.getClass()));

        out.writeChars(objectMapper.writeValueAsString(value));
    }

    @Override
    public T deserialize(final DataInput in, final int available) throws IOException {
        final String typeName = in.readUTF();
        final String discriminator = in.readUTF();
        final Class persistableClass = metadataProvider.getPersistableClass(typeName, discriminator);

        return (T) objectMapper.readValue(in.readLine(), persistableClass);
    }

}
