package c8y.trackeragent.utils;

import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
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
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.sdk.client.SDKException;

public class GroupPropertyAccessor {

    private static final Logger logger = LoggerFactory.getLogger(GroupPropertyAccessor.class);

    private static final String CONFIG_DIR_NAME = ".trackeragent";
    private static final String ENTRY_PATTERN = "%s.%s=%s";

    private final Pattern entryKeyPattern;
    private final File sourceFile;
    private final List<String> groupElementNames;
    private final HashMap<String, Group> groups = new HashMap<>();
    private Properties source;

    public GroupPropertyAccessor(String sourcePath, List<String> groupElementNames) {
        this.sourceFile = getInputFile(sourcePath);
        this.groupElementNames = groupElementNames;
        this.entryKeyPattern = createEntryKeyPattern(groupElementNames);
    }

    public void read() {
        loadData();
        groups.clear();
        for (Object entry : source.keySet()) {
            tryReadEntry(String.valueOf(entry));
        }
    }

    public void write(Group group) {
        String lineSeparator = System.getProperty("line.separator");
        try (FileWriter fileWriter = new FileWriter(sourceFile, true)) {
            fileWriter.append(lineSeparator);
            Iterable<String> lines = group.stringify();
            for (String line : lines) {
                fileWriter.append(lineSeparator);
                fileWriter.append(line);
            }
        } catch (IOException ioex) {
            throw new SDKException("Can't write to file " + sourceFile.getAbsolutePath(), ioex);
        }
        groups.put(group.getGroupName(), group);
    }

    public List<Group> getGroups() {
        return new ArrayList<>(groups.values());
    }

    public Properties getSource() {
        return source;
    }

    public Group createEmptyGroup(String groupName) {
        return new Group(groupName, groupElementNames);
    }

    private void loadData() throws SDKException {
        source = new Properties();
        try (InputStream io = new FileInputStream(sourceFile)) {
            source.load(io);
        } catch (IOException ioex) {
            throw new SDKException("Can't load configuration from file system " + sourceFile.getAbsolutePath(), ioex);
        }
    }

    private void tryReadEntry(String entry) {
        Matcher matcher = entryKeyPattern.matcher(entry);
        if (matcher.matches()) {
            String groupName = matcher.group(1);
            Group group = getOrCreateGroup(groupName);
            group.put(matcher.group(2), source.getProperty(entry));
        }
    }

    private Group getOrCreateGroup(String groupName) {
        Group group = groups.get(groupName);
        if (group == null) {
            group = createEmptyGroup(groupName);
            groups.put(groupName, group);
        }
        return group;
    }

    private static Pattern createEntryKeyPattern(List<String> groupEntryNames) {
        StringBuffer joinedEntryNames = new StringBuffer();
        Iterator<String> iter = groupEntryNames.iterator();
        while (iter.hasNext()) {
            joinedEntryNames.append(iter.next());
            if (iter.hasNext()) {
                joinedEntryNames.append("|");
            }
        }
        String regexp = String.format("(.*)\\.(%s)", joinedEntryNames.toString());
        return Pattern.compile(regexp);
    }
    
    private static File getInputFile(String relativePath) {
        String home = System.getProperty("user.home");
        Path path = FileSystems.getDefault().getPath(home, CONFIG_DIR_NAME, relativePath);
        return path.toFile();
    }

    public static class Group {

        private final Map<String, String> content = new HashMap<>();
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
            return content.get(key);
        }

        public String put(String key, String value) {
            return content.put(key, value);
        }

        public Iterable<String> stringify() {
            List<String> result = new ArrayList<String>();
            SortedSet<String> keys = new TreeSet<>(content.keySet());
            for (String key : keys) {
                result.add(format(ENTRY_PATTERN, groupName, key, get(key)));
            }
            return result;
        }

        public boolean isFullyInitialized() {
            HashSet<String> missingRecords = new HashSet<>(groupEntryNames);
            missingRecords.removeAll(content.keySet());
            boolean valid = missingRecords.isEmpty();
            if (!valid) {
                logger.error("Missing properties {} for key {}.", missingRecords, groupName);
            }
            return valid;
        }
    }

}
