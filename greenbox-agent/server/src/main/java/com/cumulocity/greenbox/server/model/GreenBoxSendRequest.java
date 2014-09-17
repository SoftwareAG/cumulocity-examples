package com.cumulocity.greenbox.server.model;

import java.util.LinkedList;
import java.util.List;

public class GreenBoxSendRequest extends GreenBoxRequest {
    private List<Measurement> data = new LinkedList<>();

    public List<Measurement> getData() {
        return data;
    }

    public void setData(List<Measurement> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "GreenBoxSendRequest [data=" + data + ", hubUID=" + getHubUID() + ", type=" + getType() + ", hub=" + getHub() + "]";
    }

}
