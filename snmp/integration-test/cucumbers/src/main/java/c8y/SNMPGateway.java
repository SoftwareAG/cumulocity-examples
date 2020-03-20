package c8y;

import lombok.Data;

@Data
public class SNMPGateway {

    private int transmitRate;
    private int pollingRate;
    private int autoDiscoveryInterval;
    private String ipRange;
}
