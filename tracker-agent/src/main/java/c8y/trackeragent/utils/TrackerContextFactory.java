package c8y.trackeragent.utils;

import static c8y.trackeragent.utils.ConfigUtils.getConfigFilePath;
import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;
import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import c8y.trackeragent.TrackerPlatform;
import c8y.trackeragent.utils.GroupPropertyAccessor.Group;

import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.SDKException;

class TrackerContextFactory {

    public static final String SOURCE_FILE = "common.properties";

    TrackerContext createTrackerContext() throws SDKException {
        GroupPropertyAccessor propertyAccessor = new GroupPropertyAccessor(
                getConfigFilePath(SOURCE_FILE), asList("host", "user", "password"));
        List<Group> groups = propertyAccessor.refresh().getGroups();
        Map<String, TrackerPlatform> platforms = asPlatforms(groups);
        return new TrackerContext(platforms, propertyAccessor.getSource());

    }

    private Map<String, TrackerPlatform> asPlatforms(List<Group> groups) {
        Map<String, TrackerPlatform> result = new HashMap<>();
        for (Group group : groups) {
            if (group.isFullyInitialized()) {
                result.put(group.getGroupName(), asPlatform(group));
            }
        }
        return result;
    }

    private TrackerPlatform asPlatform(Group group) {
        CumulocityCredentials credentials = cumulocityCredentials(
                group.get("user"), group.get("password")).withTenantId(group.getGroupName()).build();
        return new TrackerPlatform(new PlatformImpl(group.get("host"), credentials));
    }
}
