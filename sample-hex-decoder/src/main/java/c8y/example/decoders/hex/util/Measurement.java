/*
 * Copyright © 2019 Software AG, Darmstadt, Germany and/or its licensors
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package c8y.example.decoders.hex.util;

import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;

import java.util.HashMap;
import java.util.Map;

public class Measurement extends MeasurementRepresentation {

    private Map<String, Object> ensurePathExists(String path){
        String[] segments = path.split("\\.");
        Map<String, Object> level = super.getAttrs();
        for(int i = 0; i<segments.length-1; i++){
            if(!level.containsKey(segments[i])) {
                HashMap<String, Object> nextLevel = new HashMap<>();
                level.put(segments[i], nextLevel);
                level = nextLevel;
            }
            else if (level.get(segments[i]) instanceof Map) {
                level = (Map)level.get(segments[i]);
            }
        }
        return level;
    }

    public void set(String path, Object value){
        String[] segments = path.split("\\.");
        Map<String, Object> level = ensurePathExists(path);
        level.put(segments[segments.length-1], value);
    }

    public Object get(String path) {
        String[] segments = path.split("\\.");
        Object level = super.get(segments[0]);

        for (int i = 1; i < segments.length; i++) {
            level = ((HashMap<String, Object>) level).get(segments[i]);
            if (level == null)
                return null;
        }
        return level;
    }

}
