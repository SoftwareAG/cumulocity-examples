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
