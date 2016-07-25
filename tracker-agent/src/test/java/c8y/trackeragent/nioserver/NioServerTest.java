package c8y.trackeragent.nioserver;

import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NioServerTest extends NioServerTestSupport {
    
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(NioServerTest.class);

    private SocketWriter writer1;
    private SocketWriter writer2;
    private SocketWriter writer3;

    @Before
    public void before() throws Exception {
        super.before();
        writer1 = newWriter();
        writer2 = newWriter();
        writer3 = newWriter();
    }
        
    @Test
    public void shouldReadData() throws Exception {
        writer1.push("#ABC;");
        writer2.push("$123;");
        writer3.push("abcd;");
        sleep(SECONDS.toMillis(1));
        
       assertThatReportsOnSocket("#ABC"); 
       assertThatReportsOnSocket("$123"); 
       assertThatReportsOnSocket("abcd"); 
    }
    
    @Test
    public void shouldHandleClose() throws Exception {
        writer1.push("ABC");
        writer1.stop();
        sleep(SECONDS.toMillis(1));
    }
    
    
}
