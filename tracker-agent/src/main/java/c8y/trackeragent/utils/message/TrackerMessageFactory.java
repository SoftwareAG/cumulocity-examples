package c8y.trackeragent.utils.message;


public class TrackerMessageFactory {
    
    private final String fieldSep;
    private final String reportSep;
    
    public TrackerMessageFactory(String fieldSep, String reportSep) {
        this.fieldSep = fieldSep;
        this.reportSep = reportSep;
    }
    
    public TrackerMessage message(String text) {
        return new TrackerMessage(fieldSep, reportSep, text);
    }

    

}
