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
