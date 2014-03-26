package c8y.trackeragent.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.sdk.client.SDKException;

public class KeyValueDataReader {
    
    private static final Logger logger = LoggerFactory.getLogger(KeyValueDataReader.class);
    
    public final String ENTRY_REGEXP = "(.*)\\.(%s)";
    private final Pattern entryPattern;

    private final String sourcePath;
    private final List<String> groupEntryNames;
    private Properties source;
    private HashMap<String, Group> groups;
    
    public KeyValueDataReader(String sourcePath, List<String> groupEntryNames) {
        this.sourcePath = sourcePath;
        this.groupEntryNames = groupEntryNames;
        this.entryPattern = asEntryPattern();
    }

    public void init() {
        loadData();
        this.groups = new HashMap<>();
        for (Object entry : source.keySet()) {
            tryReadEntry(String.valueOf(entry));
        }
    }
    
    public List<Group> getGroups() {
        return new ArrayList<>(groups.values());
    }

    public Properties getSource() {
        return source;
    }

    private void loadData() throws SDKException {
        Properties result = new Properties();
        //TODO change to absolute
        try (InputStream is = KeyValueDataReader.class.getResourceAsStream(sourcePath); 
                InputStreamReader ir = new InputStreamReader(is)) {
            result.load(ir);
        } catch (IOException ioex) {
            new SDKException("Can't load configuration from " + sourcePath, ioex);
        }
        this.source = result;
    }
    
    private void tryReadEntry(String entry) {
        Matcher matcher = entryPattern.matcher(entry);
        if(matcher.matches()) {
            String groupName = matcher.group(1);
            Group group = getOrCreateGroup(groupName);
            group.put(matcher.group(2), source.getProperty(entry));
        }
    }

    private Group getOrCreateGroup(String groupName) {
        Group group = groups.get(groupName);
        if(group == null) {
            group = new Group(groupName, groupEntryNames);
            groups.put(groupName, group);
        }
        return group;
    }
    
    private Pattern asEntryPattern() {
        StringBuffer joinedEntryNames = new StringBuffer();
        Iterator<String> iter = groupEntryNames.iterator();
        while (iter.hasNext()) {
            joinedEntryNames.append(iter.next());
            if(iter.hasNext()) {
                joinedEntryNames.append("|");                
            }
        }
        String regexp = String.format("(.*)\\.(%s)", joinedEntryNames.toString());
        return Pattern.compile(regexp);
    }
    
    public static class Group {
        
        private final Map<String, String> properties = new HashMap<>();
        private final String groupName;
        private List<String> groupEntryNames;
        
        public Group(String groupName, List<String> groupEntryNames) {
            this.groupName = groupName;
            this.groupEntryNames = groupEntryNames;
        }
        
        public String getGroupName() {
            return groupName;
        }

        public String get(Object key) {
            return properties.get(key);
        }

        public String put(String key, String value) {
            return properties.put(key, value);
        }
        
        public boolean isFullyInitialized() {
            HashSet<String> missingRecords = new HashSet<>(groupEntryNames);
            missingRecords.removeAll(properties.keySet());
            boolean valid = missingRecords.isEmpty();
            if(!valid) {
                logger.error("Missing properties {} for key {}.", missingRecords, groupName);
            }
            return valid;
        }
    }
    
}
