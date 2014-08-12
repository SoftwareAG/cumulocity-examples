package com.cumulocity.tekelec;

import java.math.BigDecimal;

import com.cumulocity.model.measurement.MeasurementValue;

public class TEK586Measurement {

    private MeasurementValue auxRssi;
    private MeasurementValue sonicResultCode;
    
    public TEK586Measurement() {
    }

    public TEK586Measurement(int auxRssi, int sonicResultCode) {
        this.auxRssi = new MeasurementValue(BigDecimal.valueOf(auxRssi), "dBm", null, null, null);
        this.setSonicResultCode(new MeasurementValue(BigDecimal.valueOf(sonicResultCode), "", null, null, null));
    }

    public MeasurementValue getAuxRssi() {
        return auxRssi;
    }

    public void setAuxRssi(MeasurementValue auxRssi) {
        this.auxRssi = auxRssi;
    }

    public MeasurementValue getSonicResultCode() {
        return sonicResultCode;
    }

    public void setSonicResultCode(MeasurementValue sonicResultCode) {
        this.sonicResultCode = sonicResultCode;
    }

}
