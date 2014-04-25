package c8y.trackeragent;

public class TestConfiguration {
    
    private String c8yHost;
    private String c8yUser;
    private String c8yPassword;
    private String c8yTenant;
    private String trackerAgentHost;
    private int trackerAgentPort;
    
    public String getC8yHost() {
        return c8yHost;
    }
    public TestConfiguration setC8yHost(String c8yHost) {
        this.c8yHost = c8yHost;
        return this;
    }
    public String getC8yUser() {
        return c8yUser;
    }
    public TestConfiguration setC8yUser(String c8yUser) {
        this.c8yUser = c8yUser;
        return this;
    }
    public String getC8yPassword() {
        return c8yPassword;
    }
    public TestConfiguration setC8yPassword(String c8yPassword) {
        this.c8yPassword = c8yPassword;
        return this;
    }
    public String getC8yTenant() {
        return c8yTenant;
    }
    public TestConfiguration setC8yTenant(String c8yTenant) {
        this.c8yTenant = c8yTenant;
        return this;
    }
    public String getTrackerAgentHost() {
        return trackerAgentHost;
    }
    public TestConfiguration setTrackerAgentHost(String trackerAgentHost) {
        this.trackerAgentHost = trackerAgentHost;
        return this;
    }
    public int getTrackerAgentPort() {
        return trackerAgentPort;
    }
    public TestConfiguration setTrackerAgentPort(int trackerAgentPort) {
        this.trackerAgentPort = trackerAgentPort;
        return this;
    }
    @Override
    public String toString() {
        return String.format("TestConfiguration [c8yHost=%s, c8yUser=%s, c8yPassword=%s, c8yTenant=%s, trackerAgentHost=%s, trackerAgentPort=%s]", c8yHost, c8yUser, c8yPassword, c8yTenant,
                trackerAgentHost, trackerAgentPort);
    }
}
