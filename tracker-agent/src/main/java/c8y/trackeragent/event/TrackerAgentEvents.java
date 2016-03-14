package c8y.trackeragent.event;

import c8y.trackeragent.devicebootstrap.DeviceCredentials;

public class TrackerAgentEvents {

    public static class NewDeviceEvent {
        
        private final String imei;

        public NewDeviceEvent(String imei) {
            this.imei = imei;
        }

        public String getImei() {
            return imei;
        }
    }
    
    public static class NewDeviceRegisteredEvent {
    	
    	private final DeviceCredentials deviceCredentials;
    	
    	public NewDeviceRegisteredEvent(DeviceCredentials deviceCredentials) {
    		this.deviceCredentials = deviceCredentials;
    	}
    	
    	public DeviceCredentials getDeviceCredentials() {
    		return deviceCredentials;
    	}
    }
    
    public static class NewTenantEvent {
    	
    	private final String tenant;
    	
    	public NewTenantEvent(String tenant) {
    		this.tenant = tenant;
    	}

		public String getTenant() {
			return tenant;
		}
    }
    
    
    public static class NewTenantRegisteredEvent {

    	private final String tenant;

		public NewTenantRegisteredEvent(String tenant) {
			this.tenant = tenant;
		}

		public String getTenant() {
			return tenant;
		}
	}

}
