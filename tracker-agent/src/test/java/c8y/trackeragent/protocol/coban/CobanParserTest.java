package c8y.trackeragent.protocol.coban;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class CobanParserTest {
    
    @Test
    public void shouldParseEmail() throws Exception {
        String actual = new CobanParser().parse(new String[]{"**","imei:ABCD", "sth"});
        
        assertThat(actual).isEqualTo("ABCD");
        
    }

}
