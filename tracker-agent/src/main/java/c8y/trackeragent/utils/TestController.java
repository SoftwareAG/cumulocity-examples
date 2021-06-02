package c8y.trackeragent.utils;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.sdk.client.identity.IdentityApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(value = "/test")
public class TestController {

    @Autowired
    private IdentityApi identityApi;

    @RequestMapping(method = GET, consumes = APPLICATION_JSON_VALUE)
    public void get() {
        identityApi.getExternalId(GId.asGId(1));
    }
}
