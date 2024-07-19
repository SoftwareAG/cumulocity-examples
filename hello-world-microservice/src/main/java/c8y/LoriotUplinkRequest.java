package c8y;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.svenson.JSONProperty;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoriotUplinkRequest {

    private String cmd;

    @JsonProperty("EUI")
    private String EUI;

    private long ts;

    private boolean ack;

    private long bat;

    private int fcnt;

    private int port;

    private String encdata;

    private String data;

    private String dr;

    private BigDecimal rssi;

    private long freq;

    private BigDecimal snr;

    private List<Gws> gws;

    @JSONProperty(ignoreIfNull = true)
    public String getEncdata() {
        return encdata;
    }

    @JSONProperty(ignoreIfNull = true)
    public BigDecimal getSnr() {
        return snr;
    }

    @JSONProperty(ignoreIfNull = true)
    public BigDecimal getRssi() {
        return rssi;
    }

}
