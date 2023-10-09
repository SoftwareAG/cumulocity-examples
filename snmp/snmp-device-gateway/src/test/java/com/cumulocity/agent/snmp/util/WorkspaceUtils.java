package com.cumulocity.agent.snmp.util;

import lombok.experimental.UtilityClass;

import static java.util.Optional.ofNullable;

@UtilityClass
public class WorkspaceUtils {

    /**
     * @return path to write temporary files for tests.
     */
    public static String getWorkspacePath() {
        return ofNullable(System.getenv("WORKSPACE"))
                .orElseGet(() -> System.getProperty("java.io.tmpdir"));
    }
}
