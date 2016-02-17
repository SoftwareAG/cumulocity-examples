package c8y.trackeragent.context;

import java.util.Map;

public class ConnectionContext {
	
	private final Map<String, Object> connectionParams;
	private final String imei;
	
	public ConnectionContext(String imei, Map<String, Object> params) {
		this.imei = imei;
		this.connectionParams = params;
	}
	
    public Object getConnectionParam(String paramName) {
        return connectionParams.get(paramName);
    }
    
    public boolean isConnectionFlagOn(String paramName) {
        return Boolean.TRUE.equals(getConnectionParam(paramName));
    }
    
    public void setConnectionParam(String paramName, Object paramValue) {
        connectionParams.put(paramName, paramValue);
    }

	public String getImei() {
		return imei;
	}
	
	public DeviceContext getDeviceContext() {
	    return DeviceContextRegistry.get().get(imei);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((imei == null) ? 0 : imei.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnectionContext other = (ConnectionContext) obj;
		if (imei == null) {
			if (other.imei != null)
				return false;
		} else if (!imei.equals(other.imei))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ConnectionContext [connectionParams=" + connectionParams
				+ ", imei=" + imei + "]";
	}
	
}
