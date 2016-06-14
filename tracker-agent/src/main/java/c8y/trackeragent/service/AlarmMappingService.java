package c8y.trackeragent.service;



public interface AlarmMappingService {
    
    String getType(String name);
    String getText(String name, Object... args);
    String getSeverity(String type);

    

}
