package lora.codec.lansitec.decoder;

import com.cumulocity.microservice.customdecoders.api.exception.DecoderServiceException;
import com.cumulocity.microservice.customdecoders.api.model.DataFragmentUpdate;
import com.cumulocity.microservice.customdecoders.api.model.DecoderResult;
import com.cumulocity.microservice.customdecoders.api.model.MeasurementDto;
import com.cumulocity.microservice.customdecoders.api.model.MeasurementValueDto;
import com.cumulocity.microservice.customdecoders.api.service.DecoderService;
import com.cumulocity.microservice.lpwan.codec.decoder.model.LpwanDecoderInputData;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjects;
import com.google.common.io.BaseEncoding;
import lora.codec.lansitec.LansitecCodec;
import lora.codec.lansitec.algo.Algo;
import lora.codec.lansitec.model.Beacon;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LansitecDecoder implements DecoderService {

	/**
	 *
	 */
	private static final String SET_CONFIG = "set config";

	/**
	 *
	 */
	private static final String DEVICE_REQUEST = "device request";

	/**
	 *
	 */
	private static final String REGISTER_REQUEST = "register request";

	/**
	 *
	 */
	private static final String POSITION_REQUEST = "position request";

	/**
	 *
	 */
	private static final String GET_CONFIG = "get config";

	private final Logger logger = LoggerFactory.getLogger(LansitecCodec.class);

	private static final String ASSET_TRACKER = "Asset Tracker";

	enum MODE {
		AU920((byte) 0x1, (byte) 0x01), CLAA((byte) 0x2, (byte) 0x02), CN470((byte) 0x3, (byte) 0x04),
		AS923((byte) 0x4, (byte) 0x08), EU433((byte) 0x5, (byte) 0x10), EU868((byte) 0x6, (byte) 0x20),
		US915((byte) 0x7, (byte) 0x40);

		byte mode;
		byte smode;

		private MODE(byte mode, byte smode) {
			this.mode = mode;
			this.smode = smode;
		}

		static final Map<Byte, MODE> BY_MODE = new HashMap<>();
		static final Map<Byte, MODE> BY_SMODE = new HashMap<>();

		static {
			for (MODE f : values()) {
				BY_MODE.put(f.mode, f);
				BY_SMODE.put(f.smode, f);
			}
		}

		static List<String> getSupportedModes(byte smode) {
			List<String> result = new ArrayList<>();

			for (MODE m : values()) {
				if ((m.smode & smode) != 0) {
					result.add(m.name());
				}
			}

			return result;
		}
	}

	enum GPSSTATE {
		OFF((byte) 0), BOOT_GPS((byte) 1), LOCATING((byte) 2), LOCATED((byte) 3), NO_SIGNAL((byte) 9);

		byte value;

		private GPSSTATE(byte value) {
			this.value = value;
		}

		static final Map<Byte, GPSSTATE> BY_VALUE = new HashMap<>();

		static {
			for (GPSSTATE f : values()) {
				BY_VALUE.put(f.value, f);
			}
		}
	}

	enum CHGSTATE {
		POWER_CABLE_DISCONNECTED((byte) 0), CHARGING((byte) 5), CHARGE_COMPLETE((byte) 6);

		byte value;

		private CHGSTATE(byte value) {
			this.value = value;
		}

		static final Map<Byte, CHGSTATE> BY_VALUE = new HashMap<>();

		static {
			for (CHGSTATE f : values()) {
				BY_VALUE.put(f.value, f);
			}
		}
	}

	enum TYPE {
		REGISTER((byte) 0x10) {
			@Override
			public DecoderResult process(LpwanDecoderInputData decoderInput, byte type, ByteBuffer buffer, Algo algo) {
				DecoderResult decoderResult = new DecoderResult();

				String adr = (type & 8) != 0 ? "ON" : "OFF";
				MODE mode = MODE.BY_MODE.get((byte) (type & 0x7));
				byte smode = buffer.get();
				List<String> supportedModes = MODE.getSupportedModes(smode);
				byte power = (byte) (buffer.get() >> 4);
				byte cfg = buffer.get();
				String dr = "DR" + (byte) (cfg >> 4);
				String breakpoint = (cfg & 8) != 0 ? ENABLE : DISABLE;
				String selfadapt = (cfg & 4) != 0 ? ENABLE : DISABLE;
				String oneoff = (cfg & 2) != 0 ? ENABLE : DISABLE;
				String alreport = (cfg & 1) != 0 ? ENABLE : DISABLE;
				int pos = buffer.getShort() * 10;
				int hb = buffer.get() / 2;
				short crc = buffer.getShort();

				String configString = String.format(
						"ADR: %s%nMODE: %s%nSMODE: %s%nPOWER: %ddBm%nDR: %s%nBREAKPOINT: %s%nSELFADAPT: %s%nONEOFF: %s%nALREPORT: %s%nPOS: %ds%nHB: %dmn%nCRC: %d",
						adr, mode.name(), String.join(",", supportedModes), power, dr, breakpoint, selfadapt, oneoff,
						alreport, pos, hb, crc);

				decoderResult.addDataFragment(new DataFragmentUpdate("c8y_Configuration/config", configString));
				decoderResult.addDataFragment(new DataFragmentUpdate("c8y_RequiredAvailability/responseInterval", hb));

				return decoderResult;
			}
		},
		HEARTBEAT((byte) 0x20) {
			@Override
			public DecoderResult process(LpwanDecoderInputData decoderInput, byte type, ByteBuffer buffer, Algo algo) {
				DecoderResult decoderResult = new DecoderResult();

				long vol = buffer.get();
				long rssi = -buffer.get();
				long snr = 0;
				if ((type & 0x07) > 0) {
					snr = buffer.getShort();
				}
				byte gpsstat = buffer.get();
				GPSSTATE gpsState = GPSSTATE.BY_VALUE.get((byte) ((gpsstat&0xff) >> 4));
				int vibState = gpsstat & 0xf;
				byte chgstat = buffer.get();
				CHGSTATE chgState = CHGSTATE.BY_VALUE.get((byte) ((chgstat&0xff) >> 4));

				EventRepresentation event = new EventRepresentation();
				event.setType("Tracker status");
				event.setText(String.format("GPSSTATE: %s\nVIBSTATE: %d\nCHGSTATE: %s", gpsState != null ? gpsState.name() : "UNKNOWN(" + gpsstat + ")", vibState, chgState != null ? chgState.name() : "UNKNOWN(" + chgstat + ")"));
				event.setDateTime(DateTime.now());
				decoderResult.addEvent(event, false);

				MeasurementDto measurementToAdd = new MeasurementDto();
				measurementToAdd.setType("c8y_Battery");
				measurementToAdd.setSeries("c8y_Battery");
				ArrayList<MeasurementValueDto> measurementValueDtos = new ArrayList<>();
				MeasurementValueDto valueDto = new MeasurementValueDto();
				valueDto.setSeriesName("level");
				valueDto.setValue(BigDecimal.valueOf(vol));
				valueDto.setUnit("%");
				measurementValueDtos.add(valueDto);
				measurementToAdd.setValues(measurementValueDtos);
				measurementToAdd.setTime(DateTime.now());
				decoderResult.addMeasurement(measurementToAdd);

				measurementToAdd = new MeasurementDto();
				measurementToAdd.setType("Tracker Signal Strength");
				measurementToAdd.setSeries("Tracker Signal Strength");
				measurementValueDtos = new ArrayList<>();
				valueDto = new MeasurementValueDto();
				valueDto.setSeriesName("rssi");
				valueDto.setValue(BigDecimal.valueOf(rssi));
				valueDto.setUnit("dBm");
				measurementValueDtos.add(valueDto);
				measurementToAdd.setValues(measurementValueDtos);
				measurementToAdd.setTime(new DateTime(decoderInput.getUpdateTime()));
				decoderResult.addMeasurement(measurementToAdd);

				measurementToAdd = new MeasurementDto();
				measurementToAdd.setType("Tracker Signal Strength");
				measurementToAdd.setSeries("Tracker Signal Strength");
				measurementValueDtos = new ArrayList<>();
				valueDto = new MeasurementValueDto();
				valueDto.setSeriesName("snr");
				valueDto.setValue(BigDecimal.valueOf(snr));
				valueDto.setUnit("dBm");
				measurementValueDtos.add(valueDto);
				measurementToAdd.setValues(measurementValueDtos);
				measurementToAdd.setTime(new DateTime(decoderInput.getUpdateTime()));
				decoderResult.addMeasurement(measurementToAdd);

				return decoderResult;
			}
		},
		PERIODICAL_POSITION((byte) 0x30) {
			@Override
			public DecoderResult process(LpwanDecoderInputData decoderInput, byte type, ByteBuffer buffer, Algo algo) {
				DecoderResult decoderResult = new DecoderResult();

				float lng = buffer.getFloat();
				float lat = buffer.getFloat();
				long time = buffer.getInt() * 1000L;

				decoderResult.addDataFragment(new DataFragmentUpdate("c8y_Position/lat", BigDecimal.valueOf(lat)));
				decoderResult.addDataFragment(new DataFragmentUpdate("c8y_Position/lng", BigDecimal.valueOf(lng)));

				EventRepresentation event = new EventRepresentation();
				event.setType("c8y_LocationUpdate");
				event.setText("Location updated");
				event.setDateTime(new DateTime(time));
				decoderResult.addEvent(event, false);

				return decoderResult;
			}
		},
		ON_DEMAND_POSITION((byte) 0x40) {
			@Override
			public DecoderResult process(LpwanDecoderInputData decoderInput, byte type, ByteBuffer buffer, Algo algo) {
				DecoderResult decoderResult = new DecoderResult();

				buffer.get();
				float lng = buffer.getFloat();
				float lat = buffer.getFloat();
				long time = buffer.getInt() * 1000L;

				decoderResult.addDataFragment(new DataFragmentUpdate("c8y_Position/lat", BigDecimal.valueOf(lat)));
				decoderResult.addDataFragment(new DataFragmentUpdate("c8y_Position/lng", BigDecimal.valueOf(lng)));

				EventRepresentation event = new EventRepresentation();
				event.setType("c8y_LocationUpdate");
				event.setText("Location updated");
				event.setDateTime(new DateTime(time));
				decoderResult.addEvent(event, false);

				return decoderResult;
			}
		},
		HISTORY_POSITION((byte) 0x50) {
			@Override
			public DecoderResult process(LpwanDecoderInputData decoderInput, byte type, ByteBuffer buffer, Algo algo) {
				// TODO Auto-generated method stub
				return new DecoderResult();

			}
		},
		ALARM((byte) 0x60) {
			@Override
			public DecoderResult process(LpwanDecoderInputData decoderInput, byte type, ByteBuffer buffer, Algo algo) {
				// TODO Auto-generated method stub
				return new DecoderResult();

			}
		},
		BLE_COORDINATE((byte) 0x70) {
			@Override
			public DecoderResult process(LpwanDecoderInputData decoderInput, byte type, ByteBuffer buffer, Algo algo) {
				DecoderResult decoderResult = new DecoderResult();

				byte move = buffer.get();
				buffer.getInt();
				//boolean beaconChanged = false;
				List<Beacon> beacons = new ArrayList<>();
				while (buffer.hasRemaining()) {
					short major = buffer.getShort();
					short minor = buffer.getShort();
					byte rssi = buffer.get();


					EventRepresentation event = new EventRepresentation();
					event.setType("BLE coordinate");
					event.setText(String.format("MOVE: %d\nMAJOR: %04X\nMINOR: %04X\nRSSI: %d", move, major, minor, rssi));
					event.setDateTime(new DateTime(decoderInput.getUpdateTime()));
					decoderResult.addEvent(event, false);

					Beacon beacon = new Beacon(String.format("%04X", major), String.format("%04X", minor), rssi);
					beacons.add(beacon);

					MeasurementDto measurementToAdd = new MeasurementDto();
					measurementToAdd.setType("Max rssi");
					measurementToAdd.setSeries("Max rssi");
					ArrayList<MeasurementValueDto> measurementValueDtos = new ArrayList<>();
					MeasurementValueDto valueDto = new MeasurementValueDto();
					valueDto.setSeriesName("rssi");
					valueDto.setValue(BigDecimal.valueOf(beacon.getRssi()));
					valueDto.setUnit("dBm");
					measurementValueDtos.add(valueDto);
					measurementToAdd.setValues(measurementValueDtos);
					measurementToAdd.setTime(new DateTime(decoderInput.getUpdateTime()));
					decoderResult.addMeasurement(measurementToAdd);


					String fragmentName = String.format("%04X", major) + "-" + String.format("%04X", minor);
					measurementToAdd = new MeasurementDto();
					measurementToAdd.setType(fragmentName);
					measurementToAdd.setSeries(fragmentName);
					measurementValueDtos = new ArrayList<>();
					valueDto = new MeasurementValueDto();
					valueDto.setSeriesName("rssi");
					valueDto.setValue(BigDecimal.valueOf(rssi));
					valueDto.setUnit("dBm");
					measurementValueDtos.add(valueDto);
					measurementToAdd.setValues(measurementValueDtos);
					measurementToAdd.setTime(new DateTime(decoderInput.getUpdateTime()));
					decoderResult.addMeasurement(measurementToAdd);
				}

				Beacon beacon = algo.getPosition(ManagedObjects.asManagedObject(GId.asGId(decoderInput.getSourceDeviceId())), beacons);
				decoderResult.addDataFragment(new DataFragmentUpdate("lora_codec_lansitec_Beacon/major", beacon.getMajor()));
				decoderResult.addDataFragment(new DataFragmentUpdate("lora_codec_lansitec_Beacon/minor", beacon.getMinor()));
				decoderResult.addDataFragment(new DataFragmentUpdate("lora_codec_lansitec_Beacon/rssi", beacon.getRssi()));

				/*if (beacon != null) {
					if (beacon.getMajor().equals(newBeacon.getMajor()) && beacon.getMinor().equals(newBeacon.getMinor()) || newBeacon.getRssi() > beacon.getRssi()) {
						mor.set(newBeacon);
						c8yData.setMorToUpdate(mor);
						beaconChanged = true;
						beacon = newBeacon;
					}
				} else {
					mor.set(newBeacon);
					c8yData.setMorToUpdate(mor);
					beaconChanged = true;
					beacon = newBeacon;
				}
				if (beaconChanged) {
					c8yData.addEvent(mor, "Nearest beacon changed", String.format("MAJOR: %s\nMINOR: %s\nRSSI: %d", beacon.getMajor(), beacon.getMinor(), beacon.getRssi()), null, updateTime);
				}*/

				return decoderResult;
			}
		},
		ACKNOWLEDGE((byte) 0xF0) {
			@Override
			public DecoderResult process(LpwanDecoderInputData decoderInput, byte type, ByteBuffer buffer, Algo algo) {
				DecoderResult decoderResult = new DecoderResult();

				decoderResult.addDataFragment(new DataFragmentUpdate("c8y_Configuration/config", "Configuration requested..."));

				return decoderResult;
			}
		};
		/**
		 *
		 */
		private static final String DISABLE = "Disable";
		/**
		 *
		 */
		private static final String ENABLE = "Enable";
		byte type;

		private TYPE(byte type) {
			this.type = type;
		}

		static final Map<Byte, TYPE> BY_VALUE = new HashMap<>();

		static {
			for (TYPE f : values()) {
				BY_VALUE.put(f.type, f);
			}
		}

		public abstract DecoderResult process(LpwanDecoderInputData decoderInput, byte type, ByteBuffer buffer, Algo algo);

	}

	@Override
	public DecoderResult decode(String inputData, GId deviceId, Map<String, String> args) throws DecoderServiceException {
		setAlgo("maxrssi");

		LpwanDecoderInputData decoderInput = new LpwanDecoderInputData(inputData, deviceId, args);

		ByteBuffer buffer = ByteBuffer.wrap(BaseEncoding.base16().decode(inputData.toUpperCase()));

		if (decoderInput.getSourceDeviceInfo().getModel().equals(ASSET_TRACKER)) {
			byte type = buffer.get();
			TYPE t = TYPE.BY_VALUE.get((byte) (type & 0xf0));
			logger.info("Frame type: {}", t.name());
			try {
				return t.process(decoderInput, type, buffer, currentAlgo);
			} catch(Exception e) {
				logger.error(e.getMessage());
				throw new DecoderServiceException(e, e.getMessage(), DecoderResult.empty());
			}
		}

		return DecoderResult.empty();
	}

	//UGLY!!!!!! not scalable!!!!!
	//Need to store that in a managed object!!!
	private Algo currentAlgo;

	@Autowired
	private List<Algo> algos;

	public List<Algo> getAlgos() {
		return algos;
	}

	public void setAlgo(String id) {
		if (algos != null) {
			for (Algo algo : algos) {
				logger.info("Algo {} found", algo.getLabel());
				if (algo.getId().equals(id)) {
					logger.info("Algo found: {}", algo.getLabel());
					currentAlgo = algo;
				}
			}
		} else {
			logger.info("No algorithms available!!!");
		}
	}
}
