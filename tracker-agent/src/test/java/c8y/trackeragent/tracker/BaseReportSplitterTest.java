/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.tracker;

import static c8y.trackeragent.utils.ByteHelper.getBytes;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class BaseReportSplitterTest {
    
    @Test
    public void splitReports() throws Exception {
        BaseReportSplitter splitter = new BaseReportSplitter(";");
        
        assertThat(splitter.split(getBytes(""))).isEmpty();
        assertThat(splitter.split(getBytes("abc"))).containsExactly("abc");
        assertThat(splitter.split(getBytes("abc;cde"))).containsExactly("abc", "cde");
    }

}
