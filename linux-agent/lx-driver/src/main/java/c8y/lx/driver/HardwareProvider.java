package c8y.lx.driver;

import c8y.Hardware;

public interface HardwareProvider {
    
    final String UNKNOWN = "unknown";
    
    public Hardware getHardware();

}
