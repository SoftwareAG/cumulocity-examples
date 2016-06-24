package c8y.trackeragent.configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import c8y.trackeragent.protocol.mapping.TrackingProtocol;

import com.cumulocity.sdk.client.polling.PollingStrategy;

public class TrackerConfiguration {

    private String platformHost;
    private int localPort1;
    private int localPort2;
    private String bootstrapUser;
    private String bootstrapPassword;
    private String bootstrapTenant;
    private int clientTimeout;
    private List<Long> bootstrapPollIntervals;
    private Integer cobanLocationReportTimeInterval;
    private Integer rfv16LocationReportTimeInterval;
    private boolean forceInitialHost;
    
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

    public int getLocalPort1() {
        return localPort1;
    }
    

    public TrackerConfiguration setLocalPort1(int localPort1) {
        this.localPort1 = localPort1;
        return this;
    }
    
    public int getLocalPort2() {
        return localPort2;
    }
        
    public TrackerConfiguration setLocalPort2(int localPort2) {
        this.localPort2 = localPort2;
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
    
    public Integer getCobanLocationReportTimeInterval() {
        return cobanLocationReportTimeInterval;
    }

    public TrackerConfiguration setCobanLocationReportTimeInterval(Integer cobanDefaultLocationReportInterval) {
        this.cobanLocationReportTimeInterval = cobanDefaultLocationReportInterval;
        return this;
    }
    
    public Integer getRfv16LocationReportTimeInterval() {
        return rfv16LocationReportTimeInterval;
    }

    public TrackerConfiguration setRfv16LocationReportTimeInterval(Integer rfv16LocationReportTimeInterval) {
        this.rfv16LocationReportTimeInterval = rfv16LocationReportTimeInterval;
        return this;
    }

    public boolean getForceInitialHost() {
        return forceInitialHost;
    }
    
    public TrackerConfiguration setForceInitialHost(boolean forceInitialHost) {
        this.forceInitialHost = forceInitialHost;
        return this;
    }
    
    public int getPort(TrackingProtocol protocol) {
        if (getLocalPort1Protocols().contains(protocol)) {
            return getLocalPort1();
        }
        if (getLocalPort2Protocols().contains(protocol)) {
            return getLocalPort2();
        }
        throw new RuntimeException("Dont know port for protocol " + protocol);
    }
    
    public Collection<TrackingProtocol> getLocalPort1Protocols() {
        return Arrays.asList(
                TrackingProtocol.TELIC, 
                TrackingProtocol.GL200
        );
    }
    
    public Collection<TrackingProtocol> getLocalPort2Protocols() {
        return Arrays.asList(
                TrackingProtocol.COBAN, 
                TrackingProtocol.RFV16,
                TrackingProtocol.MT90G
        );
    }

    @Override
    public String toString() {
        return String
                .format("TrackerConfiguration [platformHost=%s, localPort1=%s, localPort2=%s, bootstrapUser=%s, bootstrapPassword=%s, bootstrapTenant=%s, clientTimeout=%s, bootstrapPollIntervals=%s, cobanLocationReportTimeInterval=%s, rfv16LocationReportTimeInterval=%s, forceInitialHost=%s, getLocalPort1Protocols()=%s, getLocalPort2Protocols()=%s]",
                        platformHost, localPort1, localPort2, bootstrapUser, bootstrapPassword, bootstrapTenant,
                        clientTimeout, bootstrapPollIntervals, cobanLocationReportTimeInterval,
                        rfv16LocationReportTimeInterval, forceInitialHost, getLocalPort1Protocols(), getLocalPort2Protocols());
    }
    
}
