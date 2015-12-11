package c8y.trackeragent.utils;

import java.util.Arrays;
import java.util.List;

import com.cumulocity.sdk.client.polling.PollingStrategy;

public class TrackerConfiguration {

    private String platformHost;
    private int localPort;
    private String bootstrapUser;
    private String bootstrapPassword;
    private String bootstrapTenant;
    private int clientTimeout;
    private List<Long> bootstrapPollIntervals;
    private String cobanLocationReportInterval;
    
    public TrackerConfiguration() {
        this.bootstrapPollIntervals = Arrays.asList(PollingStrategy.DEFAULT_POLL_INTERVALS);
    }

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
    
    public int getClientTimeout() {
        return clientTimeout;
    }

    public TrackerConfiguration setClientTimeout(int clientTimeout) {
        this.clientTimeout = clientTimeout;
        return this;
    }
    
    public List<Long> getBootstrapPollIntervals() {
        return bootstrapPollIntervals;
    }

    public TrackerConfiguration setBootstrapPollIntervals(List<Long> bootstrapPollIntervals) {
        this.bootstrapPollIntervals = bootstrapPollIntervals;
        return this;
    }
    
    public String getCobanLocationReportInterval() {
        return cobanLocationReportInterval;
    }

    public TrackerConfiguration setCobanLocationReportInterval(String cobanDefaultLocationReportInterval) {
        this.cobanLocationReportInterval = cobanDefaultLocationReportInterval;
        return this;
    }

    @Override
    public String toString() {
        return String
                .format("TrackerConfiguration [platformHost=%s, localPort=%s, bootstrapUser=%s, bootstrapPassword=%s, bootstrapTenant=%s, clientTimeout=%s, bootstrapPollIntervals=%s, cobanDefaultLocationReportInterval=%s]",
                        platformHost, localPort, bootstrapUser, bootstrapPassword, bootstrapTenant, clientTimeout,
                        bootstrapPollIntervals, cobanLocationReportInterval);
    }
    
}
