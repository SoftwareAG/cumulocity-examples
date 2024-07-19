package c8y;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.svenson.JSONProperty;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Gws {

    private BigDecimal rssi;
    private BigDecimal snr;
    private long ts;
    private String gweui;
    private BigDecimal lat;
    private BigDecimal lon;

    @JSONProperty(ignoreIfNull = true)
    public BigDecimal getRssi() {
        return rssi;
    }

    @JSONProperty(ignoreIfNull = true)
    public BigDecimal getSnr() {
        return snr;
    }

    @JSONProperty(ignoreIfNull = true)
    public String getGweui() {
        return gweui;
    }

    @JSONProperty(ignoreIfNull = true)
    public BigDecimal getLat() {
        return lat;
    }

    @JSONProperty(ignoreIfNull = true)
    public BigDecimal getLon() {
        return lon;
    }

}
