package c8y.mibparser.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class MibUploadResult implements Serializable {

    @JsonProperty("c8y_ModbusDeviceTypeInfo")
    private DeviceType deviceTypeInfo;

    @JsonProperty("c8y_Registers")
    private List<Register> registers;
}
