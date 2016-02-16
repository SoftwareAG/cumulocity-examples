package c8y.trackeragent.utils.message;


public class TrackerMessageFactory {
    
    private final String fieldSep;
    private final String reportSep;
    private final String reportPrefix;
    
    public TrackerMessageFactory(String fieldSep, String reportSep, String reportPrefix) {
        this.fieldSep = fieldSep;
        this.reportSep = reportSep;
        this.reportPrefix = reportPrefix;
    }
    
    public TrackerMessageFactory(String fieldSep, String reportSep) {
        this(fieldSep, reportSep, "");
    }
    
    public TrackerMessage msg() {
        return new TrackerMessage(fieldSep, reportSep, reportPrefix);
    }
    
    public TrackerMessage msg(String text) {
        return msg().fromText(text);
    }

    

}
