package c8y.lora.sdk.model;

import com.cumulocity.lpwan.devicetype.model.UplinkConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Decode {
	private String payload;
	private UplinkConfiguration uplinkConfiguration;

}
