package c8y.trackeragent.protocol.aplicomd.model;

public enum Field {
    EVENT_ID(0x0, 1),
    EVENT_INFO(0x0, 1),
    DATA_VALIDITY(0x8, 1),
    TIME(0x4, 4),
    GPS_TIME(0x8, 4),
    LATITUDE(0x8, 4),
    LONGTITUDE(0x8, 4),
    NUMBER_OF_SATELLITES(0x8, 1),
    SPEED(0x10, 1),
    MAX_SPEED(0x10, 1),
    HEADING(0x10, 1),
    DIN_STATUS(0x40, 1),
    AD1(0x20, 2),
    AD2(0x20, 2),
    AD3(0x20, 2),
    AD4(0x20, 2),
    MAIN_POWER(0x8000,2),
    EXT_BATTERY(0x8000, 2);


    private int fieldSelector;
    private int length;

    Field(int fieldSelector, int length) {
        this.fieldSelector = fieldSelector;
        this.length = length;
    }

    public int getFieldSelector() {
        return fieldSelector;
    }

    public int getLength() {
        return length;
    }
}
