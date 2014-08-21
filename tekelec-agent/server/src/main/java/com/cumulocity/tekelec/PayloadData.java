package com.cumulocity.tekelec;

public class PayloadData {
    int sonicRssi;
    int tempInCelsius;
    int sonicResultCode;
    int distance;
    
    public PayloadData() {
    }
    
    public PayloadData(int sonicRssi, int tempInCelsius, int sonicResultCode, int distance) {
        this.sonicRssi = sonicRssi;
        this.tempInCelsius = tempInCelsius;
        this.sonicResultCode = sonicResultCode;
        this.distance = distance;
    }
    
    public int getSonicRssi() {
        return sonicRssi;
    }
    public void setSonicRssi(int sonicRssi) {
        this.sonicRssi = sonicRssi;
    }
    public int getTempInCelsius() {
        return tempInCelsius;
    }
    public void setTempInCelsius(int tempInCelsius) {
        this.tempInCelsius = tempInCelsius;
    }
    public int getSonicResultCode() {
        return sonicResultCode;
    }
    public void setSonicResultCode(int sonicResultCode) {
        this.sonicResultCode = sonicResultCode;
    }
    public int getDistance() {
        return distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + distance;
        result = prime * result + sonicResultCode;
        result = prime * result + sonicRssi;
        result = prime * result + tempInCelsius;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PayloadData other = (PayloadData) obj;
        if (distance != other.distance)
            return false;
        if (sonicResultCode != other.sonicResultCode)
            return false;
        if (sonicRssi != other.sonicRssi)
            return false;
        if (tempInCelsius != other.tempInCelsius)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PayloadData [sonicRssi=" + sonicRssi + ", tempInCelsius=" + tempInCelsius + ", sonicResultCode=" + sonicResultCode
                + ", distance=" + distance + "]";
    }
}
