/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.protocol.telic;

import static c8y.trackeragent.utils.ByteHelper.getBytes;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TelicReportSplitterTest {
    
    @Test
    public void splitReports() throws Exception {
        TelicReportSplitter splitter = new TelicReportSplitter();
        assertThat(splitter.split(getBytes(leftPadding("abc\0", 28 + 4)))).containsExactly("abc");
        
        splitter = new TelicReportSplitter();
        assertThat(splitter.split(getBytes(leftPadding("abc\0", 28 + 4) + leftPadding("cde\0", 4)))).containsExactly("abc", "cde");
    }
    
    @Test
    public void shouldReturnEmptyListForTooShortReport() throws Exception {
        TelicReportSplitter splitter = new TelicReportSplitter();
        assertThat(splitter.split(getBytes("abc\0"))).isEmpty();
    }
    
    private static String leftPadding(String source, int length) {
        for (int i = 0; i < length; i++) {
            source = "_" + source;
        }
        return source;
    }

    
    

}
