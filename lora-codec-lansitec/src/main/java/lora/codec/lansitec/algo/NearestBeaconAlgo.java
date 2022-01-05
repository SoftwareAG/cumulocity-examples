/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2022 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lora.codec.lansitec.algo;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import lora.codec.lansitec.model.Beacon;

import java.util.List;

//@Component
public class NearestBeaconAlgo extends Algo {

    @Override
    public Beacon getPosition(ManagedObjectRepresentation tracker, List<Beacon> beacons) {
        Beacon beacon = null;
        for (Beacon newBeacon : beacons) {
            if (beacon != null) {
                if (beacon.getMajor().equals(newBeacon.getMajor()) && beacon.getMinor().equals(newBeacon.getMinor()) || newBeacon.getRssi() > beacon.getRssi()) {
                    beacon = newBeacon;
                }
            } else {
                beacon = newBeacon;
            }

        }
        return beacon;
    }

    @Override
    public String getId() {
        return "nearest";
    }

    @Override
    public String getLabel() {
        return "Nearest beacon";
    }
    
}
