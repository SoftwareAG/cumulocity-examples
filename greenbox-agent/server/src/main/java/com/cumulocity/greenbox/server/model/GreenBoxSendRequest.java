package com.cumulocity.greenbox.server.model;

import java.util.LinkedList;
import java.util.List;

public class GreenBoxSendRequest extends GreenBoxRequest {
    private List<Data> data = new LinkedList<>();

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "GreenBoxSendRequest [data=" + data + ", hubUID=" + getHubUID() + ", type=" + getType() + ", hub=" + getHub() + "]";
    }

}
