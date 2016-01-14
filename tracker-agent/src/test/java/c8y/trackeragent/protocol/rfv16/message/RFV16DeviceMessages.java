package c8y.trackeragent.protocol.rfv16.message;

import static c8y.trackeragent.utils.SignedLocation.latitude;
import static c8y.trackeragent.utils.SignedLocation.longitude;
import c8y.Position;
import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.utils.SignedLocation;
import c8y.trackeragent.utils.message.TrackerMessage;

public class RFV16DeviceMessages extends RFV16ServerMessages {

    private static final String DEFAULT_TRACKER_STATUS = "FFFFFFFF";

    /**
     * *XX,YYYYYYYYYY,V1,HHMMSS,S,latitude,D,longitude,G,speed,direction,DDMMYY,tracker_status#
     */
    public TrackerMessage positionUpdate(String maker, String imei, Position position) {
        SignedLocation lat = latitude().withValue(position.getLat());
        SignedLocation lng = longitude().withValue(position.getLng());
        return msg()
            .appendField(maker)
            .appendField(imei)
            .appendField(RFV16Constants.MESSAGE_TYPE_V1)
            .appendField(currTime())
            .appendField(RFV16Constants.DATE_EFFECTIVE_MARK)
            .appendField(lat.getValue())
            .appendField(lat.getSymbol())
            .appendField(lng.getValue())
            .appendField(lng.getSymbol())
            .appendField("") // empty speed
            .appendField("") // empty direction
            .appendField(currDate())
            .appendField(DEFAULT_TRACKER_STATUS);
            
    }


}
