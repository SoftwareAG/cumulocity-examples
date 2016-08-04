package c8y.trackeragent_it;

public class TestSettings {
    
    private String c8yUser;
    private String c8yPassword;
    private String c8yTenant;
    private String trackerAgentHost;
    
    public String getC8yUser() {
        return c8yUser;
    }
    public TestSettings setC8yUser(String c8yUser) {
        this.c8yUser = c8yUser;
        return this;
    }
    public String getC8yPassword() {
        return c8yPassword;
    }
    public TestSettings setC8yPassword(String c8yPassword) {
        this.c8yPassword = c8yPassword;
        return this;
    }
    public String getC8yTenant() {
        return c8yTenant;
    }
    public TestSettings setC8yTenant(String c8yTenant) {
        this.c8yTenant = c8yTenant;
        return this;
    }
    public String getTrackerAgentHost() {
        return trackerAgentHost;
    }
    public TestSettings setTrackerAgentHost(String trackerAgentHost) {
        this.trackerAgentHost = trackerAgentHost;
        return this;
    }
    @Override
    public String toString() {
        return String.format("TestConfiguration [c8yUser=%s, c8yPassword=%s, c8yTenant=%s, trackerAgentHost=%s]",
                c8yUser, c8yPassword, c8yTenant, trackerAgentHost);
    }

}
