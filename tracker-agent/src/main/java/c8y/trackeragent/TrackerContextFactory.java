package c8y.trackeragent;

import static com.cumulocity.model.authentication.CumulocityCredentials.Builder.cumulocityCredentials;
import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import c8y.trackeragent.repository.KeyValueDataReader;
import c8y.trackeragent.repository.KeyValueDataReader.Group;

import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.SDKException;

public class TrackerContextFactory {

    public static final String PROPS = "/cumulocity.properties";

    public static TrackerContext createTrackerContext() throws SDKException {
        return new TrackerContextFactory().newTrackerContext();
    }

    private TrackerContext newTrackerContext() throws SDKException {
        KeyValueDataReader keyValueDataReader = new KeyValueDataReader(PROPS, asList("host", "user", "password"));
        keyValueDataReader.init();
        List<Group> groups = keyValueDataReader.getGroups();
        Map<String, TrackerPlatform> platforms = asPlatforms(groups);
        return new TrackerContext(platforms, keyValueDataReader.getSource());

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
        Platform platform = new PlatformImpl(group.get("host"), credentials);
        return new TrackerPlatform(group.getGroupName(), platform);
    }
}
