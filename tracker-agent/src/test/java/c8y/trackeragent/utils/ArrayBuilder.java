/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.utils;

import java.util.ArrayList;
import java.util.List;

public class ArrayBuilder {
    
    private List<String> result = new ArrayList<String>();
    
    public static ArrayBuilder array() {
        return new ArrayBuilder();
    }
    
    public ArrayBuilder withValue(int index, Object value) {
        while(result.size() < index + 1) {
            result.add("");
        }
        result.set(index, value.toString());
        return this;
    }
    
    public String[] build() {
        for (int index = 0; index < result.size(); index++) {
            if (result.get(index) == null) {
                result.add(index, "");
            }
        }
        return result.toArray(new String[0]);
    }

}
