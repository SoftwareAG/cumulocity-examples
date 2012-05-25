/*
 * Copyright 2012 Nokia Siemens Networks 
 */
package com.cumulocity.agents.mps.emulator.model;

public class MeterResult {
    private int isOnline;
    private int hasRelay;
    private int type;
    private int isDeleleted;
    private int isProfied;
    
    public MeterResult(int isOnline, int hasRelay, int type, int isDeleleted, int isProfied) {
        this.isOnline = isOnline;
        this.hasRelay = hasRelay;
        this.type = type;
        this.isDeleleted = isDeleleted;
        this.isProfied = isProfied;
    }
    
    public int getIsOnline() {
        return isOnline;
    }
    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }
    public int getHasRelay() {
        return hasRelay;
    }
    public void setHasRelay(int hasRelay) {
        this.hasRelay = hasRelay;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getIsDeleleted() {
        return isDeleleted;
    }
    public void setIsDeleleted(int isDeleleted) {
        this.isDeleleted = isDeleleted;
    }
    public int getIsProfied() {
        return isProfied;
    }
    public void setIsProfied(int isProfied) {
        this.isProfied = isProfied;
    }
    
    
}
