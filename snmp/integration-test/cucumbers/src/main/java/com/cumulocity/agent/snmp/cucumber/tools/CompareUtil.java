/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, 
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

package com.cumulocity.agent.snmp.cucumber.tools;

import java.util.Arrays;
import java.util.Collection;

public class CompareUtil {

    public static void checkExistence(int count, int actualSize, AssertionType... assertionTypes) {
        Collection<AssertionType> assertionTypeList = Arrays.asList(assertionTypes);
        if (assertionTypeList.contains(AssertionType.EXACT)) {
            if (count != actualSize) {
                throw new AssertionError(String.format("Expecting equals but the number of items in result list is different from expected. Expected: %s, Actual: %s", count, actualSize));
            }
        }
        if (assertionTypeList.contains(AssertionType.AT_LEAST)) {
            if (count > actualSize) {
                throw new AssertionError(String.format("Expecting AT_LEAST but the number of items in result list is different from expected. Expected: %s, Actual: %s", count, actualSize));
            }
        }
        if (assertionTypeList.contains(AssertionType.AT_MAX)) {
            if (count < actualSize) {
                throw new AssertionError(String.format("Expecting AT_MAX but the number of items in result list is different from expected. Expected: %s, Actual: %s", count, actualSize));
            }
        }
    }
}
