package c8y.trackeragent.protocol.mapping;

public enum TrackerProtocol {
    
    TELIC {
        @Override
        public boolean accept(byte firstByte) {
            return firstByte >= '0' && firstByte <= '9';
        }
        
    },
    GL200 {
        @Override
        public boolean accept(byte firstByte) {
            return firstByte < '0' || firstByte > '9';
        }
        
    },
    COBAN {
        @Override
        public boolean accept(byte firstByte) {
            return firstByte == '#';
        }
        
    },
    RFV16 {
        @Override
        public boolean accept(byte firstByte) {
            return firstByte == '*';
        }
        
    };
    
    public abstract boolean accept(byte firstByte);
}
