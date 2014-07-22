package com.cumulocity.tixi.server.model.txml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "External")
public class External {

	@XmlElement(name = "Bus")
	private List<Bus> buses = new ArrayList<>();
	
	public List<Bus> getBuses() {
		return buses;
	}
	public void setBuses(List<Bus> buses) {
		this.buses = buses;
	}
	
	@Override
    public String toString() {
	    return String.format("External [buses=%s]", buses);
    }

	public static class Bus {
		
		@XmlElement(name = "Device")
		private List<Device> devices = new ArrayList<>();
		
		@XmlAttribute(name = "Name")
		private String name;
		
		public Bus() {}
		
		public Bus(String name) {
	        this.name = name;
        }

		public List<Device> getDevices() {
			return devices;
		}

		public void setDevices(List<Device> devices) {
			this.devices = devices;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
        public String toString() {
	        return String.format("Bus [name=%s]", name);
        }

		@Override
        public int hashCode() {
	        final int prime = 31;
	        int result = 1;
	        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
	        Bus other = (Bus) obj;
	        if (name == null) {
		        if (other.name != null)
			        return false;
	        } else if (!name.equals(other.name))
		        return false;
	        return true;
        }
	}
	
	public static class Device {
		
		@XmlElement(name = "Meter")
		private List<Meter> meters = new ArrayList<>();
		
		@XmlAttribute(name = "Name")
		private String name;
		
		public Device() {
        }
		
		public Device(String name) {
	        this.name = name;
        }

		public List<Meter> getMeters() {
			return meters;
		}

		public void setMeters(List<Meter> meters) {
			this.meters = meters;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
        public String toString() {
	        return String.format("Device [name=%s]", name);
        }

		@Override
        public int hashCode() {
	        final int prime = 31;
	        int result = 1;
	        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
	        Device other = (Device) obj;
	        if (name == null) {
		        if (other.name != null)
			        return false;
	        } else if (!name.equals(other.name))
		        return false;
	        return true;
        }
	}
	
	public static class Meter extends LogBaseItem {

		public Meter() {
	        super();
        }

		public Meter(String id) {
	        super(id);
        }
	}

}
