package c8y.trackeragent.server;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * This controller was created to test tracking-agent deployed to the platform as a microservice.
 * <p>
 * For deployed microservices only HTTP communication is allowed and only through whitelisted ports. This controller
 * bypasses the restrictions by transmitting received messages to the ports at localhost on which the tracker server is
 * listening.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    /**
     * Tests the microservice by sending a valid Coban login message to localhost:9092, on which presumably the tracker
     * server is listening. Returns the response of the tracker server.
     *
     * @param imei imei of the hypothetical device sending the message
     * @see <a href="https://drive.google.com/file/d/1wU3tOZ-Ets7RqbvharhEhyMb6IZGpy2R/view">coban protocol</a>
     */
    @RequestMapping(path = "test1", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> sendCobanLoginMsgToTrackerServer(
            @RequestParam(required = false) String imei)
            throws Exception {

        if (imei == null) {
            imei = "359586015829802"; // as in the example in the Coban specs
        }
        Preconditions.checkArgument(imei.matches("\\d+"));

        return sendCustomMsgToTrackerServer(9092, "##,imei:" + imei + ",A;");
    }

    /**
     * Tests the microservice by sending the message to the given port at localhost, on which presumably the tracker
     * server is listening. Returns the response of the tracker server.
     */
    @RequestMapping(path = "test2", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> sendCustomMsgToTrackerServer(
            @RequestParam int port,
            @RequestParam String message)
            throws Exception {

        Socket s = new Socket();
        s.connect(new InetSocketAddress("localhost", port));

        log.info("Sending message [{}] to http://localhost:[{}] ...", message, port);
        s.getOutputStream().write(message.getBytes(StandardCharsets.UTF_8));

        log.info("Waiting for the response...");
        char[] ch = new char[1024];
        new InputStreamReader(s.getInputStream()).read(ch);
        return new ResponseEntity<>(new String(ch).trim(), HttpStatus.OK);
    }
}