package c8y.trackeragent_it;

public class TestSettings {

    private String c8yUser;
    private String c8yPassword;
    private String c8yTenant;
    private String c8yHost;
    private String trackerAgentHost;
    private String bootstrapUser;
    private String bootstrapPassword;

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

    public String getC8yHost() {
        return c8yHost;
    }

    public TestSettings setC8yHost(String c8yHost) {
        this.c8yHost = c8yHost;
        return this;
    }
    
    public String getBootstrapUser() {
        return bootstrapUser;
    }

    public TestSettings setBootstrapUser(String bootstrapUser) {
        this.bootstrapUser = bootstrapUser;
        return this;
    }

    public String getBootstrapPassword() {
        return bootstrapPassword;
    }

    public TestSettings setBootstrapPassword(String bootstrapPassword) {
        this.bootstrapPassword = bootstrapPassword;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TestSettings [c8yUser=");
        builder.append(c8yUser);
        builder.append(", c8yPassword=");
        builder.append(c8yPassword);
        builder.append(", c8yTenant=");
        builder.append(c8yTenant);
        builder.append(", c8yHost=");
        builder.append(c8yHost);
        builder.append(", trackerAgentHost=");
        builder.append(trackerAgentHost);
        builder.append(", bootstrapUser=");
        builder.append(bootstrapUser);
        builder.append(", bootstrapPassword=");
        builder.append(bootstrapPassword);
        builder.append("]");
        return builder.toString();
    }


}
