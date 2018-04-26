package com.cumulocity.route.model.core;

import com.cumulocity.model.idtype.GId;
import lombok.Data;
import org.joda.time.DateTime;

@Data
public class CreateEvent extends HasSource {
    private final GId source;
    private final String type;
    private final DateTime time;
}
