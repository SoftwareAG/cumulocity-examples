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

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * This controller was created to test tracking-agent deployed to the platform as a regular microservice.
 * <p>
 * Since in that case only standard communication via HTTP is allowed this controller is needed to bypass
 * the platform restrictions and simulate messages sent from the tracking devices.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    /**
     * Tests the microservice by sending a valid Coban login message to the local socket on port 9092.
     * <p>
     * Check the logs to see the result.
     *
     * @param imei imei of the hypothetical device sending heartbeat
     *
     * @see <a href="https://drive.google.com/file/d/1wU3tOZ-Ets7RqbvharhEhyMb6IZGpy2R/view">coban protocol</a>
     */
    @RequestMapping(path = "test1", method = RequestMethod.POST)
    public ResponseEntity<Void> testBySendingCobanHeartbeat(@RequestParam(required = false) String imei) throws Exception {
        if (imei == null) {
            imei = "359586015829802";
        }
        Preconditions.checkArgument(imei.matches("\\d+"));

        log.info("Sending valid Coban heartbeat message to http://localhost:9092 ...");

        Socket s = new Socket();
        s.connect(new InetSocketAddress("localhost", 9092));
        s.getOutputStream().write(("##,imei:" + imei + ",A;").getBytes(StandardCharsets.UTF_8));
        s.close();

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    /**
     * Tests the microservice to the local socket on given port. Returns the message and the response for it.
     */
    @RequestMapping(path = "test2", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> testBySendingCustomMessage(@RequestParam int port, @RequestParam String message) throws Exception {

        log.info("Sending message [{}] to http://localhost:[{}] ...", message, port);

        Socket s = new Socket();

        // send the message
        s.connect(new InetSocketAddress("localhost", 9092));
        s.getOutputStream().write(message.getBytes(StandardCharsets.UTF_8));

        log.info("Sleeping for 30 seconds...");
        Thread.sleep(30000);

        // read the response if any
        s.setSoTimeout(5000);
        try {
            return new ResponseEntity<>(new String(s.getInputStream().readAllBytes(), StandardCharsets.UTF_8), HttpStatus.OK);
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            s.close();
        }

        return new ResponseEntity<>(null, HttpStatus.I_AM_A_TEAPOT);
    }

}
