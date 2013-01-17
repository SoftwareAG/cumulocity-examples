/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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
