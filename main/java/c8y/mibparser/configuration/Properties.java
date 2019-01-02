package c8y.mibparser.configuration;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Properties {
    private String applicationName;
    private String baseUrl;
    private boolean forceInitialHost;
    private String tenant;
    private String user;
    private String password;
}
