package com.cumulocity.tekelec;

import java.math.BigDecimal;

import com.cumulocity.model.measurement.MeasurementValue;

public class TEK586Measurement {

    private MeasurementValue sonicRssi;
    private MeasurementValue sonicResultCode;
    
    public TEK586Measurement() {
    }

    public TEK586Measurement(int sonicRssi, int sonicResultCode) {
        this.sonicRssi = new MeasurementValue(BigDecimal.valueOf(sonicRssi), "No", null, null, null);
        this.setSonicResultCode(new MeasurementValue(BigDecimal.valueOf(sonicResultCode), "No", null, null, null));
    }

    public MeasurementValue getSonicRssi() {
        return sonicRssi;
    }

    public void setSonicRssi(MeasurementValue sonicRssi) {
        this.sonicRssi = sonicRssi;
    }

    public MeasurementValue getSonicResultCode() {
        return sonicResultCode;
    }

    public void setSonicResultCode(MeasurementValue sonicResultCode) {
        this.sonicResultCode = sonicResultCode;
    }

}
