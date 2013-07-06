package c8y.trackeragent;

public class BatteryReportParameters extends PowerReportParametes {

    private String batteryVoltage;
    private String gpsAccuracy;
    private String speed;
    private String azimuth;
    private String altitude;
    private String longitude;
    private String latitude;
    private String gpsUtcTime;
    private String mcc;
    private String mnc;
    private String lac;
    private String cellId;
    private String odoMileage;
    
    public BatteryReportParameters(String[] parameters) {
        int i=0;
        setResp(parameters[i++]);
        setProtocolVersion(parameters[i++]);
        setImei(parameters[i++]);
        setDeviceName(parameters[i++]);
        setGpsAccuracy(parameters[i++]);
        setSpeed(parameters[i++]);
        setAzimuth(parameters[i++]);
        setAltitude(parameters[i++]);
        setLongitude(parameters[i++]);
        setLatitude(parameters[i++]);
        setGpsUtcTime(parameters[i++]);
        setMcc(parameters[i++]);
        setMnc(parameters[i++]);
        setLac(parameters[i++]);
        setCellId(parameters[i++]);
        setOdoMilage(parameters[i++]);
        setSendTime(parameters[i++]);
        setCountNumber(parameters[i++]);
    }

    public BatteryReportParameters(String resp, String protocolVersion, String imei, String deviceName, String reportId,String reportType, String number, String gpsAccuracy,String speed, String azimuth, String altitude,String longitude, String latitude,String gpsUtcTime, String mcc, String mnc, String lac, String cellId,String odoMillage, String batteryPercentage,String sendTime,String countNumber){
        this.setResp(resp);
        this.setProtocolVersion(protocolVersion);
        this.setImei(imei);
        this.setDeviceName(deviceName);
        this.setGpsAccuracy(gpsAccuracy);
        this.setSpeed(speed);
        this.setAzimuth(azimuth);
        this.setAltitude(altitude);
        this.setLongitude(longitude);
        this.setLatitude(latitude);
        this.setGpsUtcTime(gpsUtcTime);
        this.setMcc(mcc);
        this.setMnc(mnc);
        this.setLac(lac);
        this.setCellId(cellId);
        this.setOdoMilage(odoMillage);
        this.setSendTime(sendTime);
        this.setCountNumber(countNumber);
    }
 
    public BatteryReportParameters(){
        this.setResp(null);
        this.setProtocolVersion(null);
        this.setImei(null);
        this.setDeviceName(null);
        this.setGpsAccuracy(null);
        this.setSpeed(null);
        this.setAzimuth(null);
        this.setAltitude(null);
        this.setLongitude(null);
        this.setLatitude(null);
        this.setGpsUtcTime(null);
        this.setMcc(null);
        this.setMnc(null);
        this.setLac(null);
        this.setCellId(null);
        this.setOdoMilage(null);
        this.setSendTime(null);
        this.setCountNumber(null);
    }
    
    
    public String getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(String batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getGpsAccuracy() {
        return gpsAccuracy;
    }

    public void setGpsAccuracy(String gpsAccuracy) {
        this.gpsAccuracy = gpsAccuracy;
    }

    public String getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(String azimuth) {
        this.azimuth = azimuth;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getGpsUtcTime() {
        return gpsUtcTime;
    }

    public void setGpsUtcTime(String gpsUtcTime) {
        this.gpsUtcTime = gpsUtcTime;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getLac() {
        return lac;
    }

    public void setLac(String lac) {
        this.lac = lac;
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public String getOdoMilage() {
        return odoMileage;
    }

    public void setOdoMilage(String odoMilage) {
        this.odoMileage = odoMilage;
    }

}
