package c8y.trackeragent;



public class ReportParameters {
        private String resp;
        private String protocolVersion;
        private String imei;
        private String deviceName;
        private String reportId;  //id of the GEO  repr
        private String reportType; // type of report
        private String number;
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
        private String batteryPercentage;
        private String sendTime;
        private String countNumber;
        private String tailCharacter;
        
        public ReportParameters(String[] parameters) {
            int i=0;
            setResp(parameters[i++]);
            setProtocolVersion(parameters[i++]);
            setImei(parameters[i++]);
            setDeviceName(parameters[i++]);
            setReportId(parameters[i++]);
            setReportType(parameters[i++]);
            setNumber(parameters[i++]);
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
            setBatteryPercentage(parameters[i++]);
            setSendTime(parameters[i++]);
            setCountNumber(parameters[i++]);
            setTailCharacter(parameters[i++]);
        }
        
        public ReportParameters(String resp, String protocolVersion, String imei, String deviceName, String reportId,String reportType, String number, String gpsAccuracy,String speed, String azimuth, String altitude,String longitude, String latitude,String gpsUtcTime, String mcc, String mnc, String lac, String cellId,String odoMillage, String batteryPercentage,String sendTime,String countNumber, String tailCharacter){
            this.setResp(resp);
            this.setProtocolVersion(protocolVersion);
            this.setImei(imei);
            this.setDeviceName(deviceName);
            this.setReportId(reportId);
            this.setReportType(reportType);
            this.setNumber(number);
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
            this.setBatteryPercentage(batteryPercentage);
            this.setSendTime(sendTime);
            this.setCountNumber(countNumber);
            this.setTailCharacter(tailCharacter);
        }
        
        public ReportParameters(){
            this.setResp(null);
            this.setProtocolVersion(null);
            this.setImei(null);
            this.setDeviceName(null);
            this.setReportId(null);
            this.setNumber(null);
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
            this.setBatteryPercentage(null);
            this.setSendTime(null);
            this.setCountNumber(null);
            this.setTailCharacter(null);
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

        public String getReportId() {
            return reportId;
        }

        public void setReportId(String reportId) {
            this.reportId = reportId;
        }

        public String getReportType() {
            return reportType;
        }

        public void setReportType(String reportType) {
            this.reportType = reportType;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
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

        public String getBatteryPercentage() {
            return batteryPercentage;
        }

        public void setBatteryPercentage(String batteryPercentage) {
            this.batteryPercentage = batteryPercentage;
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

        public String getTailCharacter() {
            return tailCharacter;
        }

        public void setTailCharacter(String tailCharacter) {
            this.tailCharacter = tailCharacter;
        }

}
