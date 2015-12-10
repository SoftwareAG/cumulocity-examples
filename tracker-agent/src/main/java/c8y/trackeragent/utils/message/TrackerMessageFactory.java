package c8y.trackeragent.utils.message;


public class TrackerMessageFactory {
    
    private final String fieldSep;
    private final String reportSep;
    
    public TrackerMessageFactory(String fieldSep, String reportSep) {
        this.fieldSep = fieldSep;
        this.reportSep = reportSep;
    }
    
    public TrackerMessage msg() {
        return new TrackerMessage(fieldSep, reportSep);
    }
    
    public TrackerMessage msg(String text) {
        return msg().fromText(text);
    }

    

}
