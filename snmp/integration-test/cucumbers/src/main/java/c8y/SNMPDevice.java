package c8y;

import com.cumulocity.agent.snmp.cucumber.model.SnmpDeviceAuthentication;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SNMPDevice {
    private String id;
    private String ipAddress;
    private int port;
    private String type;
    private int version;
    private SnmpDeviceAuthentication auth;

    public SNMPDevice(String ipAddress, int port, String type, int version) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.type = type;
        this.version = version;
    }

}
