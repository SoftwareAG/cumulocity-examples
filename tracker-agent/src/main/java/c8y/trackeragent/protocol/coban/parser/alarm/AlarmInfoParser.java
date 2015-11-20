package c8y.trackeragent.protocol.coban.parser.alarm;


public abstract class AlarmInfoParser {
    
    protected final String type;
    
    public AlarmInfoParser(String type) {
        this.type = type;
    }

    public abstract AlarmInfo parse(String typeEntry);
    
    public static AlarmInfoParser exact(String type) {
        return new Exact(type);
    }
    
    public static AlarmInfoParser prefix(String type, String paramSeparator) {
        return new Prefix(type, paramSeparator);
    }

    private static class Exact extends AlarmInfoParser {

        public Exact(String type) {
            super(type);
        }

        @Override
        public AlarmInfo parse(String typeEntry) {
            return type.equals(typeEntry) ? new AlarmInfo(type) : null; 
        }
        
    }
    
    private static class Prefix extends AlarmInfoParser {
        
        private final String paramSeparator;
        
        public Prefix(String type, String paramSeparator) {
            super(type);
            this.paramSeparator = paramSeparator;
        }
        
        @Override
        public AlarmInfo parse(String typeEntry) {
            String[] parts = typeEntry.split(paramSeparator);
            String type = parts[0];
            String param = parts.length >= 2 ? parts[1] : null;
            return this.type.equals(type) ? new AlarmInfo(type, param) : null; 
        }
        
    }
}
