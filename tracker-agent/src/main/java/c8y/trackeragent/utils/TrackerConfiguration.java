package c8y.trackeragent.utils;

public class TrackerConfiguration {

    private String platformHost;
    private int localPort;
    private String bootstrapUser;
    private String bootstrapPassword;
    private String bootstrapTenant;

    public String getPlatformHost() {
        return platformHost;
    }

    public TrackerConfiguration setPlatformHost(String platformHost) {
        this.platformHost = platformHost;
        return this;
    }

    public int getLocalPort() {
        return localPort;
    }

    public TrackerConfiguration setLocalPort(int localPort) {
        this.localPort = localPort;
        return this;
    }

    public String getBootstrapUser() {
        return bootstrapUser;
    }

    public TrackerConfiguration setBootstrapUser(String bootstrapUser) {
        this.bootstrapUser = bootstrapUser;
        return this;
    }

    public String getBootstrapPassword() {
        return bootstrapPassword;
    }

    public TrackerConfiguration setBootstrapPassword(String bootstrapPassword) {
        this.bootstrapPassword = bootstrapPassword;
        return this;
    }

    public TrackerConfiguration setBootstrapTenant(String bootstrapTenant) {
        this.bootstrapTenant = bootstrapTenant;
        return this;
    }

    public String getBootstrapTenant() {
        return bootstrapTenant;
    }

    @Override
    public String toString() {
        return String.format("TrackerConfiguration [platformHost=%s, localPort=%s, bootstrapUser=%s, bootstrapPassword=%s, bootstrapTenant=%s]", platformHost, localPort, bootstrapUser,
                bootstrapPassword, bootstrapTenant);
    }
    
}
