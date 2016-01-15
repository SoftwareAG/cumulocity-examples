package c8y.trackeragent.protocol.rfv16.parser;

public enum AlarmType {
    
    NOISE_SENSOR(0, 0),
    DOOR(2, 0),
    THEFT(3, 0),
    LOW_BATTERY(1, 1),
    SOS(3, 1),
    OTHER(1, 2),
    OVERSPEED(3, 2),
    CHARGER_REMOVED(0, 4),
    ENTER_FENCE_AREA(3, 4),
    OUT_OF_FENCE(3, 7);
    
    private int byteNo;
    private int bitNo;
    
    private AlarmType(int byteNo, int bitNo) {
        this.byteNo = byteNo;
        this.bitNo = bitNo;
    }

    public int getByteNo() {
        return byteNo;
    }

    public void setByteNo(int byteNo) {
        this.byteNo = byteNo;
    }

    public int getBitNo() {
        return bitNo;
    }

    public void setBitNo(int bitNo) {
        this.bitNo = bitNo;
    }

}
