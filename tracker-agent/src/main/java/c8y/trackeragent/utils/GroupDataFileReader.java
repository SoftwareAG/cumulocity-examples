package c8y.trackeragent.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
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

public class GroupDataFileReader {
    
    private static final Logger logger = LoggerFactory.getLogger(GroupDataFileReader.class);
    
    private static final String CONFIG_DIR_NAME = ".trackeragent";
    
    private final Pattern entryPattern;
    private final String sourcePath;
    private final List<String> groupEntryNames;
    private final HashMap<String, Group> groups = new HashMap<>();
    private Properties source  = new Properties();
    
    public GroupDataFileReader(String sourcePath, List<String> groupEntryNames) {
        this.sourcePath = sourcePath;
        this.groupEntryNames = groupEntryNames;
        this.entryPattern = createEntryPattern();
    }

    public void init() {
        loadData();
        groups.clear();
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
        source = loadDataFromResources();
        if(source == null) {
            source = loadDataFromFileSystem();
        }
    }
    
    private Properties loadDataFromFileSystem() {
        String home = System.getProperty("user.home");
        Path path = FileSystems.getDefault().getPath(home, CONFIG_DIR_NAME, sourcePath);
        Properties source = new Properties();
        try (InputStream io = new FileInputStream(path.toFile())) {
            source.load(io);
            return source;
        } catch (IOException ioex) {
            throw new SDKException("Can't load configuration from file system " + path.toAbsolutePath().toString(), ioex);
        }
        
    }
    private Properties loadDataFromResources() {
        Properties source = null;
        try (InputStream io = GroupDataFileReader.class.getResourceAsStream(sourcePath)) {
            if(io != null) {
                source = new Properties();
                source.load(io);
            }
        } catch (IOException ioex) {
            throw new SDKException("Can't load configuration from resources " + sourcePath, ioex);
        } 
        return source;                
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
    
    private Pattern createEntryPattern() {
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
