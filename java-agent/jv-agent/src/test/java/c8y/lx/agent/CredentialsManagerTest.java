package c8y.lx.agent;

import com.cumulocity.model.authentication.CumulocityBasicCredentials;
import org.junit.After;
import org.junit.Test;

import java.io.File;

import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.fest.assertions.Assertions.assertThat;

public class CredentialsManagerTest {

    private static final String COMMON_PROPS = "./target/cumulocity.properties";
    private static final String DEVICE_PROPS = "./target/device.properties";

    @After
    public void tearDown() {
        deleteQuietly(new File(DEVICE_PROPS));
        deleteQuietly(new File(COMMON_PROPS));
    }

    @Test
    public void shouldGetBootstrapCredentials() {
        CumulocityBasicCredentials credentials = aCredentialsManager().getBootstrapCredentials();

        assertThat(credentials.getUsername()).isEqualTo("devicebootstrap");
        assertThat(credentials.getPassword()).isNotNull();
        assertThat(credentials.getTenantId()).isEqualTo("management");
    }

    @Test
    public void shouldSaveDeviceCredentials() {
        CumulocityBasicCredentials credentials = CumulocityBasicCredentials.builder()
                .tenantId("johnland")
                .username("John")
                .password("secret")
                .build();

        aCredentialsManager().saveDeviceCredentials(credentials);

        CumulocityBasicCredentials actual = aCredentialsManager().getDeviceCredentials();
        assertThat(actual.getUsername()).isEqualTo("John");
        assertThat(actual.getPassword()).isEqualTo("secret");
        assertThat(actual.getTenantId()).isEqualTo("johnland");
    }

    private static CredentialsManager aCredentialsManager() {
        return new CredentialsManager(COMMON_PROPS, DEVICE_PROPS);
    }

}
