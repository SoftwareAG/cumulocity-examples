package c8y.trackeragent.utils;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.exception.SDKExceptions;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;


public class SignedLocation {
    
    private static Logger logger = LoggerFactory.getLogger(SignedLocation.class);
    
    private final BiMap<Integer, String> signumToOrientation;
    private BigDecimal value;
    
    protected SignedLocation(String positiveToken, String negationToken) {
        this.signumToOrientation = ImmutableBiMap.of(1, positiveToken, -1, negationToken);
    }
    
    public static SignedLocation latitude() {
        return new SignedLocation("N", "S");
    }
    
    public static SignedLocation longitude() {
        return new SignedLocation("E", "W");
    }
    
    public static SignedLocation altitude() {
        return new SignedLocation("+", "-");
    }
        
    public BigDecimal getValue() {
        return value;
    }

    public SignedLocation withValue(BigDecimal value) {
        this.value = value;
        return this;
    }
    
    public SignedLocation withValue(String abs) {
        try {
            this.value = new BigDecimal(abs).abs();
        } catch (NumberFormatException nfex) {
            logger.error("Cant parse value " + abs + " to number!");
            throw SDKExceptions.narrow(nfex, "Cant parse value '" + abs + "' to number!");
        }
        return this;
    }
    
    public SignedLocation withValue(String abs, String signum) {
        withValue(abs);        
        int requestedSignum = signumToOrientation.inverse().get(signum);
        if (requestedSignum < 0) {
            value = value.negate();
        }
        return this;
    }
    
    public String getAbsValue() {
        return value.abs().toString();
    }
    
    public String getSign() {
        return signumToOrientation.get(value.signum());
    }

}
