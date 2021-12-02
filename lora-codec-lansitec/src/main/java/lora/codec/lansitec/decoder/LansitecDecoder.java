package lora.codec.lansitec.decoder;

import c8y.Configuration;
import c8y.Position;
import c8y.RequiredAvailability;
import com.cumulocity.lpwan.codec.decoder.Decoder;
import com.cumulocity.lpwan.codec.decoder.model.DecoderInput;
import com.cumulocity.lpwan.codec.decoder.model.DecoderOutput;
import com.cumulocity.lpwan.codec.exception.DecoderException;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
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

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LansitecDecoder implements Decoder {

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
			public DecoderOutput process(DecoderInput decoderInput, byte type, ByteBuffer buffer, Algo algo) {
				DecoderOutput decoderOutput = new DecoderOutput();

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

				ManagedObjectRepresentation deviceManagedObjectToUpdate = ManagedObjects.asManagedObject(decoderInput.getDeviceMoIdAsGId());
				deviceManagedObjectToUpdate.set(new Configuration(configString));
				deviceManagedObjectToUpdate.set(new RequiredAvailability(hb));
				decoderOutput.setDeviceManagedObjectToUpdate(deviceManagedObjectToUpdate);

				return decoderOutput;
			}
		},
		HEARTBEAT((byte) 0x20) {
			@Override
			public DecoderOutput process(DecoderInput decoderInput, byte type, ByteBuffer buffer, Algo algo) {
				DecoderOutput decoderOutput = new DecoderOutput();

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

				decoderOutput.addEventToCreate(decoderInput.getDeviceMoIdAsGId(), "Tracker status", String.format("GPSSTATE: %s\nVIBSTATE: %d\nCHGSTATE: %s", gpsState != null ? gpsState.name() : "UNKNOWN(" + gpsstat + ")", vibState, chgState != null ? chgState.name() : "UNKNOWN(" + chgstat + ")"), null, DateTime.now());

				decoderOutput.addMeasurementToCreate(decoderInput.getDeviceMoIdAsGId(), "c8y_Battery", "c8y_Battery", "level", BigDecimal.valueOf(vol), "%", DateTime.now());
				decoderOutput.addMeasurementToCreate(decoderInput.getDeviceMoIdAsGId(), "Tracker Signal Strength", "Tracker Signal Strength", "rssi", BigDecimal.valueOf(rssi), "dBm", new DateTime(decoderInput.getUpdateTime()));
				decoderOutput.addMeasurementToCreate(decoderInput.getDeviceMoIdAsGId(), "Tracker Signal Strength", "Tracker Signal Strength", "snr", BigDecimal.valueOf(rssi), "dBm", new DateTime(decoderInput.getUpdateTime()));

				return decoderOutput;
			}
		},
		PERIODICAL_POSITION((byte) 0x30) {
			@Override
			public DecoderOutput process(DecoderInput decoderInput, byte type, ByteBuffer buffer, Algo algo) {
				DecoderOutput decoderOutput = new DecoderOutput();

				float lng = buffer.getFloat();
				float lat = buffer.getFloat();
				long time = buffer.getInt() * 1000L;
				Position p = new Position();
				p.setLat(BigDecimal.valueOf(lat));
				p.setLng(BigDecimal.valueOf(lng));

				ManagedObjectRepresentation deviceManagedObjectToUpdate = ManagedObjects.asManagedObject(decoderInput.getDeviceMoIdAsGId());
				deviceManagedObjectToUpdate.set(p);
				decoderOutput.setDeviceManagedObjectToUpdate(deviceManagedObjectToUpdate);

				decoderOutput.addEventToCreate(decoderInput.getDeviceMoIdAsGId(), "c8y_LocationUpdate", "Location updated", null, new DateTime(time))
						.set(p);

				return decoderOutput;
			}
		},
		ON_DEMAND_POSITION((byte) 0x40) {
			@Override
			public DecoderOutput process(DecoderInput decoderInput, byte type, ByteBuffer buffer, Algo algo) {
				DecoderOutput decoderOutput = new DecoderOutput();

				buffer.get();
				float lng = buffer.getFloat();
				float lat = buffer.getFloat();
				long time = buffer.getInt() * 1000L;
				Position p = new Position();
				p.setLat(BigDecimal.valueOf(lat));
				p.setLng(BigDecimal.valueOf(lng));

				ManagedObjectRepresentation deviceManagedObjectToUpdate = ManagedObjects.asManagedObject(decoderInput.getDeviceMoIdAsGId());
				deviceManagedObjectToUpdate.set(p);
				decoderOutput.setDeviceManagedObjectToUpdate(deviceManagedObjectToUpdate);

				decoderOutput.addEventToCreate(decoderInput.getDeviceMoIdAsGId(), "c8y_LocationUpdate", "Location updated", null, new DateTime(time))
						.set(p);

				return decoderOutput;
			}
		},
		HISTORY_POSITION((byte) 0x50) {
			@Override
			public DecoderOutput process(DecoderInput decoderInput, byte type, ByteBuffer buffer, Algo algo) {
				// TODO Auto-generated method stub
				return new DecoderOutput();

			}
		},
		ALARM((byte) 0x60) {
			@Override
			public DecoderOutput process(DecoderInput decoderInput, byte type, ByteBuffer buffer, Algo algo) {
				// TODO Auto-generated method stub
				return new DecoderOutput();

			}
		},
		BLE_COORDINATE((byte) 0x70) {
			@Override
			public DecoderOutput process(DecoderInput decoderInput, byte type, ByteBuffer buffer, Algo algo) {
				DecoderOutput decoderOutput = new DecoderOutput();

				Beacon beacon = null;
				byte move = buffer.get();
				buffer.getInt();
				//boolean beaconChanged = false;
				List<Beacon> beacons = new ArrayList<>();
				while (buffer.hasRemaining()) {
					short major = buffer.getShort();
					short minor = buffer.getShort();
					byte rssi = buffer.get();

					decoderOutput.addEventToCreate(decoderInput.getDeviceMoIdAsGId(), "BLE coordinate", String.format("MOVE: %d\nMAJOR: %04X\nMINOR: %04X\nRSSI: %d", move, major, minor, rssi), null, new DateTime(decoderInput.getUpdateTime()));

					beacon = new Beacon(String.format("%04X", major), String.format("%04X", minor), rssi);
					beacons.add(beacon);

					decoderOutput.addMeasurementToCreate(decoderInput.getDeviceMoIdAsGId(), "Max rssi", "Max rssi", "rssi", BigDecimal.valueOf(beacon.getRssi()), "dBm", new DateTime(decoderInput.getUpdateTime()));

					String fragmentName = String.format("%04X", major) + "-" + String.format("%04X", minor);
					decoderOutput.addMeasurementToCreate(decoderInput.getDeviceMoIdAsGId(), fragmentName, fragmentName, "rssi", BigDecimal.valueOf(rssi), "dBm", new DateTime(decoderInput.getUpdateTime()));
				}

				ManagedObjectRepresentation deviceManagedObjectToUpdate = ManagedObjects.asManagedObject(decoderInput.getDeviceMoIdAsGId());
				deviceManagedObjectToUpdate.set(algo.getPosition(deviceManagedObjectToUpdate, beacons));
				decoderOutput.setDeviceManagedObjectToUpdate(deviceManagedObjectToUpdate);

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

				return decoderOutput;
			}
		},
		ACKNOWLEDGE((byte) 0xF0) {
			@Override
			public DecoderOutput process(DecoderInput decoderInput, byte type, ByteBuffer buffer, Algo algo) {
				DecoderOutput decoderOutput = new DecoderOutput();

				ManagedObjectRepresentation deviceManagedObjectToUpdate = ManagedObjects.asManagedObject(decoderInput.getDeviceMoIdAsGId());
				deviceManagedObjectToUpdate.set(new Configuration("Configuration requested..."));
				decoderOutput.setDeviceManagedObjectToUpdate(deviceManagedObjectToUpdate);

				return decoderOutput;
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

		public abstract DecoderOutput process(DecoderInput decoderInput, byte type, ByteBuffer buffer, Algo algo);

	}

	@Override
	public @NotNull DecoderOutput decode(@NotNull DecoderInput decoderInput) throws DecoderException {
		setAlgo("maxrssi");

		ByteBuffer buffer = ByteBuffer.wrap(BaseEncoding.base16().decode(decoderInput.getPayload().toUpperCase()));

		if (decoderInput.getDeviceInfo().getModel().equals(ASSET_TRACKER)) {
			byte type = buffer.get();
			TYPE t = TYPE.BY_VALUE.get((byte) (type & 0xf0));
			logger.info("Frame type: {}", t.name());
			try {
				return t.process(decoderInput, type, buffer, currentAlgo);
			} catch(Exception e) {
				logger.error(e.getMessage());
				new DecoderException(e.getMessage(), e);
			}
		}

		return new DecoderOutput();
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
