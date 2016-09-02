package c8y.trackeragent.tracker;

import static c8y.trackeragent.utils.ByteHelper.getBytes;
import static org.fest.assertions.Assertions.assertThat;

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
