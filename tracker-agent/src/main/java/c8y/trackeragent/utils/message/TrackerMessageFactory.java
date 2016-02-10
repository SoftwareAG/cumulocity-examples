package c8y.trackeragent.utils.message;


public abstract class TrackerMessageFactory<M extends TrackerMessage> {
        
    public abstract M msg();
    
    public TrackerMessage msg(String text) {
        return msg().fromText(text);
    }

    

}
