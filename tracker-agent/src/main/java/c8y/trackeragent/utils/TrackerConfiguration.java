package c8y.trackeragent.utils;

public class TrackerConfiguration {

    private String platformHost;
    private int localPort;
    private String bootstrapUser;
    private String bootstrapPassword;
    private String bootstrapTenant;
    private int clientTimeout;
    private String proxy;

    private int proxyport;
    private String devicePassword;


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

	public String getProxy() {
		return proxy;
	}

	public TrackerConfiguration setProxy(String proxy) {

		this.proxy = proxy;
		return this;
	}

	public int getProxyport() {
		return proxyport;
	}

	public TrackerConfiguration setProxyport(int proxyport) {
		
		this.proxyport = proxyport;
		
		return this;
	}

	public TrackerConfiguration setProxyport(String proxyport) {
		try {
			this.proxyport = new Integer(proxyport);
		} catch (NumberFormatException e) {
			// Proxy Port not configured

		}
		return this;
	}
	
	
    public String getDevicePassword() {
		return devicePassword;
	}

	public TrackerConfiguration setDevicePassword(String devicePassword) {
		this.devicePassword = devicePassword;
		return this;
	}

	@Override
    public String toString() {
        return String.format("TrackerConfiguration [platformHost=%s, localPort=%s, bootstrapUser=%s, bootstrapPassword=%s, bootstrapTenant=%s, clientTimeout=%s]", platformHost, localPort,
                bootstrapUser, bootstrapPassword, bootstrapTenant, clientTimeout);
    }
    
}
