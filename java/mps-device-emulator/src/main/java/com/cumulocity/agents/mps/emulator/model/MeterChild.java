package com.cumulocity.agents.mps.emulator.model;

public class MeterChild {
    private boolean leaf;
    private long id;
    private long name;
    
    public MeterChild(boolean leaf, long id, long name) {
        this.leaf = leaf;
        this.id = id;
        this.name = name;
    }
    public boolean isLeaf() {
        return leaf;
    }
    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getName() {
        return name;
    }
    public void setName(long name) {
        this.name = name;
    }
}
