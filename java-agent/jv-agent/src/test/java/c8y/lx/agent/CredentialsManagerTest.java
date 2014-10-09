package c8y.lx.agent;

import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.fest.assertions.Assertions.assertThat;

import java.io.File;

import org.junit.After;
import org.junit.Test;

import com.cumulocity.model.authentication.CumulocityCredentials;

public class CredentialsManagerTest {
    
    private static final String COMMON_PROPS = "./target/cumulocity.properties";
    private static final String DEVICE_PROPS = "./target/device.properties";

    @After
    public void tearDown() {
        deleteQuietly(new File(DEVICE_PROPS));
        deleteQuietly(new File(COMMON_PROPS));
    }
    
    @Test
    public void shouldGetBootstrapCredentials() throws Exception {
        CumulocityCredentials credentials = aCredentialsManager().getBootstrapCredentials();
        
        assertThat(credentials.getUsername()).isEqualTo("devicebootstrap");
        assertThat(credentials.getPassword()).isNotNull();
        assertThat(credentials.getTenantId()).isEqualTo("management");
    }
    
    @Test
    public void shouldSaveDeviceCredentials() throws Exception {
        CumulocityCredentials credentials = CumulocityCredentials.Builder.cumulocityCredentials("John", "secret")
                .withTenantId("johnland")
                .build();
        
        aCredentialsManager().saveDeviceCredentials(credentials);
        
        CumulocityCredentials actual = aCredentialsManager().getDeviceCredentials();
        assertThat(actual.getUsername()).isEqualTo("John");
        assertThat(actual.getPassword()).isEqualTo("secret");
        assertThat(actual.getTenantId()).isEqualTo("johnland");
    }
    
    private static CredentialsManager aCredentialsManager() {
        return new CredentialsManager(COMMON_PROPS, DEVICE_PROPS);
    }

}
