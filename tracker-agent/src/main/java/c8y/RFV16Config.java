package c8y;

public class RFV16Config {

    private String sosNumber;
    private Boolean vibrationAlarmArm;
    private Boolean noiseAlarmArm;
    private Boolean doorAlarmArm;
    private Boolean sosAlarmArm;

    

    public RFV16Config(RFV16Config source) {
        this.sosNumber = source.getSosNumber();
        this.vibrationAlarmArm = source.getVibrationAlarmArm();
        this.noiseAlarmArm = source.getNoiseAlarmArm();
        this.doorAlarmArm = source.getDoorAlarmArm();
        this.sosAlarmArm = source.getSosAlarmArm();
    }

    public RFV16Config() {
    }

    public String getSosNumber() {
        return sosNumber;
    }

    public void setSosNumber(String sosNumber) {
        this.sosNumber = sosNumber;
    }

    public Boolean getNoiseAlarmArm() {
        return noiseAlarmArm;
    }

    public void setNoiseAlarmArm(Boolean noiseAlarmArm) {
        this.noiseAlarmArm = noiseAlarmArm;
    }

    public Boolean getDoorAlarmArm() {
        return doorAlarmArm;
    }

    public void setDoorAlarmArm(Boolean doorAlarmArm) {
        this.doorAlarmArm = doorAlarmArm;
    }

    public Boolean getSosAlarmArm() {
        return sosAlarmArm;
    }

    public void setSosAlarmArm(Boolean sosAlarmArm) {
        this.sosAlarmArm = sosAlarmArm;
    }

    public Boolean getVibrationAlarmArm() {
        return vibrationAlarmArm;
    }

    public void setVibrationAlarmArm(Boolean vibrationAlarmArm) {
        this.vibrationAlarmArm = vibrationAlarmArm;
    }

    @Override
    public String toString() {
        return "RFV16Config [sosNumber=" + sosNumber + ", vibrationAlarmArm=" + vibrationAlarmArm + ", noiseAlarmArm=" + noiseAlarmArm
                + ", doorAlarmArm=" + doorAlarmArm + ", sosAlarmArm=" + sosAlarmArm + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((doorAlarmArm == null) ? 0 : doorAlarmArm.hashCode());
        result = prime * result + ((noiseAlarmArm == null) ? 0 : noiseAlarmArm.hashCode());
        result = prime * result + ((sosAlarmArm == null) ? 0 : sosAlarmArm.hashCode());
        result = prime * result + ((sosNumber == null) ? 0 : sosNumber.hashCode());
        result = prime * result + ((vibrationAlarmArm == null) ? 0 : vibrationAlarmArm.hashCode());
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
        RFV16Config other = (RFV16Config) obj;
        if (doorAlarmArm == null) {
            if (other.doorAlarmArm != null)
                return false;
        } else if (!doorAlarmArm.equals(other.doorAlarmArm))
            return false;
        if (noiseAlarmArm == null) {
            if (other.noiseAlarmArm != null)
                return false;
        } else if (!noiseAlarmArm.equals(other.noiseAlarmArm))
            return false;
        if (sosAlarmArm == null) {
            if (other.sosAlarmArm != null)
                return false;
        } else if (!sosAlarmArm.equals(other.sosAlarmArm))
            return false;
        if (sosNumber == null) {
            if (other.sosNumber != null)
                return false;
        } else if (!sosNumber.equals(other.sosNumber))
            return false;
        if (vibrationAlarmArm == null) {
            if (other.vibrationAlarmArm != null)
                return false;
        } else if (!vibrationAlarmArm.equals(other.vibrationAlarmArm))
            return false;
        return true;
    }
}
