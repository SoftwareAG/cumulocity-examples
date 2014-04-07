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

    public void setPlatformHost(String platformHost) {
        this.platformHost = platformHost;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public String getBootstrapUser() {
        return bootstrapUser;
    }

    public void setBootstrapUser(String bootstrapUser) {
        this.bootstrapUser = bootstrapUser;
    }

    public String getBootstrapPassword() {
        return bootstrapPassword;
    }

    public void setBootstrapPassword(String bootstrapPassword) {
        this.bootstrapPassword = bootstrapPassword;
    }

    public void setBootstrapTenant(String bootstrapTenant) {
        this.bootstrapTenant = bootstrapTenant;
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
