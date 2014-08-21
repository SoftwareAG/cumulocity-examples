package com.cumulocity.tekelec;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TekelecRequest {

    int productType;
    int hardwareRevision;
    int firmwareRevision;
    List<String> contactReason = new ArrayList<>();
    List<String> alarmAndStatus = new ArrayList<>();
    int gsmRssi;
    BigDecimal battery;
    String imei;
    byte messageType;
    int loggerSpeed;
    List<PayloadData> payloadData = new ArrayList<>();
    
    public int getProductType() {
        return productType;
    }
    public void setProductType(int productType) {
        this.productType = productType;
    }
    public int getHardwareRevision() {
        return hardwareRevision;
    }
    public void setHardwareRevision(int hardwareRevision) {
        this.hardwareRevision = hardwareRevision;
    }
    public int getFirmwareRevision() {
        return firmwareRevision;
    }
    public void setFirmwareRevision(int firmwareRevision) {
        this.firmwareRevision = firmwareRevision;
    }
    public List<String> getContactReason() {
        return contactReason;
    }
    public void setContactReason(List<String> contactReason) {
        this.contactReason = contactReason;
    }
    public List<String> getAlarmAndStatus() {
        return alarmAndStatus;
    }
    public void setAlarmAndStatus(List<String> alarmAndStatus) {
        this.alarmAndStatus = alarmAndStatus;
    }
    public int getGsmRssi() {
        return gsmRssi;
    }
    public void setGsmRssi(int gsmRssi) {
        this.gsmRssi = gsmRssi;
    }
    public BigDecimal getBattery() {
        return battery;
    }
    public void setBattery(BigDecimal battery) {
        this.battery = battery;
    }
    public String getImei() {
        return imei;
    }
    public void setImei(String imei) {
        this.imei = imei;
    }
    public byte getMessageType() {
        return messageType;
    }
    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }
    public int getLoggerSpeed() {
        return loggerSpeed;
    }
    public void setLoggerSpeed(int loggerSpeed) {
        this.loggerSpeed = loggerSpeed;
    }
    public List<PayloadData> getPayloadData() {
        return payloadData;
    }
    public void setPayloadData(List<PayloadData> payloadData) {
        this.payloadData = payloadData;
    }
    public void addPayloadData(PayloadData data) {
        payloadData.add(data);
    }
    @Override
    public String toString() {
        return "TekelecRequest [productType=" + productType + ", hardwareRevision=" + hardwareRevision + ", firmwareRevision="
                + firmwareRevision + ", contactReason=" + contactReason + ", alarmAndStatus=" + alarmAndStatus + ", gsmRssi=" + gsmRssi
                + ", battery=" + battery + ", imei=" + imei + ", messageType=" + messageType + ", loggerSpeed=" + loggerSpeed
                + ", payloadData=" + payloadData + "]";
    }
}
