package c8y.trackeragent;

public class PowerReportParametes {

    private String resp;
    private String protocolVersion;
    private String imei;
    private String deviceName;
    private String sendTime;
    private String countNumber;
    
    public PowerReportParametes(String[] parameters) {
        int i=0;
        setResp(parameters[i++]);
        setProtocolVersion(parameters[i++]);
        setImei(parameters[i++]);
        setDeviceName(parameters[i++]);
        setSendTime(parameters[i++]);
        setCountNumber(parameters[i++]);
    }

    public PowerReportParametes(String resp, String protocolVersion, String imei, String deviceName, String reportId,String reportType, String number, String gpsAccuracy,String speed, String azimuth, String altitude,String longitude, String latitude,String gpsUtcTime, String mcc, String mnc, String lac, String cellId,String odoMillage, String batteryPercentage,String sendTime,String countNumber){
        this.setResp(resp);
        this.setProtocolVersion(protocolVersion);
        this.setImei(imei);
        this.setDeviceName(deviceName);
        this.setSendTime(sendTime);
        this.setCountNumber(countNumber);
    }
 
    public PowerReportParametes(){
        this.setResp(null);
        this.setProtocolVersion(null);
        this.setImei(null);
        this.setDeviceName(null);
        this.setSendTime(null);
        this.setCountNumber(null);
    }
    
    public String getResp() {
        return resp;
    }

    public void setResp(String resp) {
        this.resp = resp;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    
    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getCountNumber() {
        return countNumber;
    }

    public void setCountNumber(String countNumber) {
        this.countNumber = countNumber;
    }

}
