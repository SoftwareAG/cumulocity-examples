package c8y.trackeragent.utils;

import static c8y.trackeragent.utils.ByteHelper.getBytes;
import static c8y.trackeragent.utils.ByteHelper.getString;
import static c8y.trackeragent.utils.ByteHelper.stripHead;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ByteHelperTest {
    
    @Test
    public void shouldTransformAsciiToAndFromBytes() throws Exception {
        assertThat(getString(getBytes("abc"))).isEqualTo("abc");
    }
    
    @Test
    public void shouldStripBytes() throws Exception {
        assertThat(stripHead(getBytes("abc"), 1)).isEqualTo(getBytes("bc"));
        assertThat(stripHead(getBytes("abc"), 2)).isEqualTo(getBytes("c"));
        assertThat(stripHead(getBytes("abc"), 3)).isEqualTo(getBytes(""));
        assertThat(stripHead(getBytes("abc"), 4)).isNull();
    }

}
