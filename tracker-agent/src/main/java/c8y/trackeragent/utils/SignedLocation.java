package c8y.trackeragent.utils;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c8y.trackeragent.exception.SDKExceptions;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;


public class SignedLocation {
    
    private static Logger logger = LoggerFactory.getLogger(SignedLocation.class);
    
    private final BiMap<Integer, String> signumToSymbol;
    private BigDecimal value;
    
    protected SignedLocation(String positiveSymbol, String negationSymbol) {
        this.signumToSymbol = ImmutableBiMap.of(1, positiveSymbol, -1, negationSymbol);
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
    
    public SignedLocation withValue(String abs, String symbol) {
        withValue(abs);        
        int requestedSignum = signumToSymbol.inverse().get(symbol);
        if (requestedSignum < 0) {
            value = value.negate();
        }
        return this;
    }
    
    public String getAbsValue() {
        return value.abs().toString();
    }
    
    public String getSymbol() {
        return signumToSymbol.get(value.signum());
    }

}
