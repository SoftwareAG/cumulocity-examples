package com.cumulocity.greenbox.server.model.json;

import static com.google.common.base.Functions.toStringFunction;
import static com.google.common.base.Optional.fromNullable;

import com.cumulocity.greenbox.server.model.CommandType;
import com.cumulocity.greenbox.server.model.GreenBoxSendRequest;
import com.cumulocity.greenbox.server.model.GreenBoxSetupRequest;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;

public class GreenBoxRequestTypeResolver extends TypeIdResolverBase implements TypeIdResolver {

    @Override
    public String idFromValue(Object value) {
        return null;
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return fromNullable(value).transform(toStringFunction()).get();
    }


    @Override
    public Id getMechanism() {
        return Id.CUSTOM;
    }

    @Override
    public JavaType typeFromId(String id) {
        return Optional.fromNullable(id).transform(asClass()).get();
    }

    private Function<String, JavaType> asClass() {
        return new Function<String, JavaType>() {

            @Override
            public JavaType apply(String input) {
                switch (CommandType.forValue(input)) {
                case SEND:
                    return SimpleType.construct(GreenBoxSendRequest.class);
                case SETUP:
                    return SimpleType.construct(GreenBoxSetupRequest.class);
                }
                throw new IllegalArgumentException(" can't resolve type for " + input);
            }
        };
    }
}
