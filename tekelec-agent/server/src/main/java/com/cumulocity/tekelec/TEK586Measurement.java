package com.cumulocity.tekelec;

public class TEK586Measurement {

    private int auxRssi;
    
    public TEK586Measurement() {
    }

    public TEK586Measurement(int auxRssi) {
        this.auxRssi = auxRssi;
    }

    public int getAuxRssi() {
        return auxRssi;
    }

    public void setAuxRssi(int auxRssi) {
        this.auxRssi = auxRssi;
    }

}
