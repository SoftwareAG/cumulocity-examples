/*
 * Copyright 2012 Nokia Siemens Networks 
 */
package com.cumulocity.agents.mps.emulator.model;

import java.util.ArrayList;
import java.util.List;

public class MeterList {
    private String id;
    private String name;
    private List<MeterChild> children = new ArrayList<MeterChild>();
    
    public static final MeterList SAMPLE_LIST = new MeterList();
    
    static {
        SAMPLE_LIST.setId("Meter List");
        SAMPLE_LIST.setName("Meter List");
        SAMPLE_LIST.getChildren().add(new MeterChild(true, 11374857, 11374857));
        SAMPLE_LIST.getChildren().add(new MeterChild(true, 9510273, 9510273));
        SAMPLE_LIST.getChildren().add(new MeterChild(true, 9510274, 9510274));
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<MeterChild> getChildren() {
        return children;
    }
    public void setChildren(List<MeterChild> children) {
        this.children = children;
    }
}
