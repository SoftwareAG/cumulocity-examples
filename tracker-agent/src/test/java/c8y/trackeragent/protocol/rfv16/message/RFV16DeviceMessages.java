package c8y.trackeragent.protocol.rfv16.message;

import static c8y.trackeragent.utils.SignedLocation.latitude;
import static c8y.trackeragent.utils.SignedLocation.longitude;

import org.joda.time.DateTime;

import c8y.Position;
import c8y.trackeragent.protocol.rfv16.RFV16Constants;
import c8y.trackeragent.utils.SignedLocation;
import c8y.trackeragent.utils.message.TrackerMessage;

public class RFV16DeviceMessages extends RFV16ServerMessages {

    private static final String DEFAULT_TRACKER_STATUS = "FFFFFFFF";

    /**
     * *XX,YYYYYYYYYY,V1,HHMMSS,S,latitude,D,longitude,G,speed,direction,DDMMYY,tracker_status#
     * 
     * 0:   maker
     * 1:   imei
     * 2:   type
     * 3:   time
     * 4:   effective mark
     * 5:   lat
     * 6:   lat symbol
     * 7:   lng
     * 8:   lng symbol
     * 9:   speed
     * 10:  direction
     * 11:  date
     * 12:  status
     * 
     */
    public TrackerMessage positionUpdate(String maker, String imei, Position position) {
        return positionUpdate(maker, imei, position, DEFAULT_TRACKER_STATUS);
    }
    
    public TrackerMessage positionUpdate(String maker, String imei, Position position, String status) {
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
                .appendField(status);
    }
    
    /**
     * 
     * *XX,YYYYYYYYYY,LINK,HHMMSS,GSM,GPS,BAT,STEP,TURNOVER,DDMMYY,tracker_status#
     * gsm： gsm signal (1-100%) 
     * gps： number GPS satellites (1-12)
     * BAT： battery lift  （1-100）
     * STEP: step (none--upload 0)
     * TURNOVER: (none--upload-0)
     * For example: 
     * *HQ,1451260840,LINK,061720,31,10,100,10000,10,090714,FFFFFFFF#
     * 
     */
    public TrackerMessage heartbeat(String maker, String imei) {
        return heartbeat(maker, imei, DEFAULT_TRACKER_STATUS);
    }
    
    public TrackerMessage heartbeat(String maker, String imei, String status) {
        return msg()
                .appendField(maker)
                .appendField(imei)
                .appendField(RFV16Constants.MESSAGE_TYPE_LINK)
                .appendField(currTime())
                .appendField("") //GSM
                .appendField("") //GPS
                .appendField("") //BAT
                .appendField("") //STEP
                .appendField("") //TUNROVER
                .appendField(currDate())  
                .appendField(status);
    }
    
    public TrackerMessage heartbeat(String maker, String imei, Integer gsmPercentage, Integer bateryPercentage) {
        return msg()
                .appendField(maker)
                .appendField(imei)
                .appendField(RFV16Constants.MESSAGE_TYPE_LINK)
                .appendField(currTime())
                .appendField(nullToEmpty(gsmPercentage))    //GSM 
                .appendField("") //GPS
                .appendField(nullToEmpty(bateryPercentage)) //BAT
                .appendField("") //STEP
                .appendField("") //TUNROVER
                .appendField(currDate())  
                .appendField(DEFAULT_TRACKER_STATUS);
    }

    /**
     * @param maker
     * @param imei
     * @param gsm       [0;31] 
     * @param batery    [0;6]
     * @return
     */
    public TrackerMessage deviceSituation(String maker, String imei, Integer gsm, Integer batery) {
        return msg()
                .appendField(maker)
                .appendField(imei)
                .appendField(RFV16Constants.MESSAGE_TYPE_V4)
                .appendField(RFV16Constants.COMMAND_DISPLAY_DEVICE_SITUATION) 
                .appendField(nullToEmpty(gsm))//GSM 
                .appendField("")//GPS 
                .appendField(nullToEmpty(batery))//BAT 
                .appendField("")//SCF 
                .appendField("")//ANS 
                .appendField("")//LIG 
                .appendField("")//MOD 
                .appendField("")//LAG 
                .appendField("");//DND 
    }
    
    public TrackerMessage confirmPositionMonitoringCommand(String maker, String imei) {
        return msg()
                .appendField(maker)
                .appendField(imei)
                .appendField(RFV16Constants.MESSAGE_TYPE_V4)
                .appendField(RFV16Constants.COMMAND_POSITION_MONITORING) 
                .appendField(prevTime())
                .appendField(currTime())
                .appendField("")
                .appendField("")
                .appendField("")
                .appendField("")
                .appendField("")
                .appendField("")
                .appendField("")
                .appendField(currDate())
                .appendField(DEFAULT_TRACKER_STATUS);                
    }
    
    private static String nullToEmpty(Object obj) {
        return obj == null ? "" : obj.toString();
    }
    
    protected String prevTime() {
        return HHMMSS.print(new DateTime(0));
    }

}
