package c8y.trackeragent.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * This controller was created to test tracking-agent deployed to the platform as regular microservice. Since in that
 * case only standard communication via HTTP is allowed this controller bypasses the platform restrictions and simulates
 * messages sent from the tracking devices.
 */
@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String test() throws IOException {
        log.info("I am here!");

        Socket s = new Socket();
        s.connect(new InetSocketAddress("localhost", 9092));

        s.getOutputStream().write("##,imei:359586015829802,A;".getBytes(StandardCharsets.UTF_8));

        s.close();

        return "{}";
    }

}
