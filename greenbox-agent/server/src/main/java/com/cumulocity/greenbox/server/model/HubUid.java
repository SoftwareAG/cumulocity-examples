package com.cumulocity.greenbox.server.model;

import com.cumulocity.model.ID;
import com.google.common.base.Function;
import com.google.common.base.Optional;

public class HubUid extends ID {

    private static final long serialVersionUID = 1L;

    private static final String TYPE = "c8y_GreenBoxHubUID";

    public HubUid(String value) {
        super(TYPE, value);
    }

    public static HubUid asHubUid(String value) {
        return Optional.fromNullable(value).transform(asHubUid()).orNull();
    }

    public static String asString(HubUid value) {
        return Optional.fromNullable(value).transform(asString()).orNull();
    }

    public static Function<HubUid, String> asString() {
        return new Function<HubUid, String>() {

            @Override
            public String apply(HubUid input) {
                return input.getValue();
            }
        };
    }

    public static Function<String, HubUid> asHubUid() {
        return new Function<String, HubUid>() {

            @Override
            public HubUid apply(String input) {
                return new HubUid(input);
            }
        };
    }
}
