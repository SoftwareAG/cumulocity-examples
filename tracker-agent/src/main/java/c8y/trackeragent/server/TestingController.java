package c8y.trackeragent.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller was created to test tracking-agent deployed to the platform as regular microservice. Since in that
 * case only standard communication via HTTP is allowed this controller bypasses the platform restrictions and simulates
 * messages sent from the tracking devices.
 */
@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestingController {

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String test() {
        log.info("I am here!");
        return "{}";
    }

}
