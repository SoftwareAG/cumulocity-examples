/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.utils;

import static java.lang.String.format;

import java.io.FileWriter;
import java.io.IOException;
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

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.sdk.client.SDKException;

import c8y.trackeragent.configuration.ConfigUtils;

/**
 * TODO: use JSON format instead key-value format 
 * 
 * @author dombiel
 *
 */
public class GroupPropertyAccessor {

    private static final Logger logger = LoggerFactory.getLogger(GroupPropertyAccessor.class);

    private static final String ENTRY_PATTERN = "%s.%s=%s";

    private final Pattern entryKeyPattern;
    private final String sourceFilePath;
    private final List<String> groupElementNames;
    private final HashMap<String, Group> groups = new HashMap<String, Group>();
    private Properties source;

    public GroupPropertyAccessor(String sourceFilePath, List<String> groupElementNames) {
        this.sourceFilePath = sourceFilePath;
        this.groupElementNames = groupElementNames;
        this.entryKeyPattern = createEntryKeyPattern(groupElementNames);
    }

    public GroupPropertyAccessor refresh() {
        source = ConfigUtils.getProperties(sourceFilePath);
        groups.clear();
        for (Object entry : source.keySet()) {
            tryReadEntry(String.valueOf(entry));
        }
        return this;
    }

    public void write(Group group) {
        String lineSeparator = System.getProperty("line.separator");
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(sourceFilePath, true);
            fileWriter.append(lineSeparator);
            Iterable<String> lines = group.stringify();
            for (String line : lines) {
                fileWriter.append(lineSeparator);
                fileWriter.append(line);
            }
        } catch (IOException ioex) {
            throw new SDKException("Can't write to file " + sourceFilePath, ioex);
        } finally {
            IOUtils.closeQuietly(fileWriter);
        }
        groups.put(group.getName(), group);
    }

    public List<Group> getGroups() {
        return new ArrayList<Group>(groups.values());
    }

    public Properties getSource() {
        return source;
    }

    public Group createEmptyGroup(String groupName) {
        return new Group(groupName, groupElementNames);
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
    
    public static class Group {

        private final Map<String, String> content = new HashMap<String, String>();
        private final String name;
        private List<String> entryNames;

        public Group(String name, List<String> entryNames) {
            this.name = name;
            this.entryNames = entryNames;
        }

        public String getName() {
            return name;
        }

        public String get(Object key) {
            return content.get(key);
        }

        public String put(String key, String value) {
            return content.put(key, value);
        }

        public Iterable<String> stringify() {
            List<String> result = new ArrayList<String>();
            SortedSet<String> keys = new TreeSet<String>(content.keySet());
            for (String key : keys) {
                result.add(format(ENTRY_PATTERN, name, key, get(key)));
            }
            return result;
        }

        public boolean isFullyInitialized() {
            HashSet<String> missingRecords = new HashSet<String>(entryNames);
            missingRecords.removeAll(content.keySet());
            boolean valid = missingRecords.isEmpty();
            if (!valid) {
                logger.error("Missing properties {} for key {}.", missingRecords, name);
            }
            return valid;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((content == null) ? 0 : content.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Group other = (Group) obj;
            if (content == null) {
                if (other.content != null)
                    return false;
            } else if (!content.equals(other.content))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return String.format("Group [content=%s, groupName=%s]", content, name);
        }
    }
}