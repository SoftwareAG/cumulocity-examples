package com.cumulocity.greenbox.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataPointReference {
    
    @JsonProperty("data_point_id")
    private int dataPointId;

    private int sequence;

    public int getDataPointId() {
        return dataPointId;
    }

    public void setDataPointId(int dataPointId) {
        this.dataPointId = dataPointId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

}
