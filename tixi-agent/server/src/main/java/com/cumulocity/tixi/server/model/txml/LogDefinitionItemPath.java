package com.cumulocity.tixi.server.model.txml;

public class LogDefinitionItemPath {

	private String agentId;
	private String deviceId;
	private String name;
	
	public LogDefinitionItemPath() {}

	public LogDefinitionItemPath(String agentId, String deviceId, String name) {
	    this.agentId = agentId;
	    this.deviceId = deviceId;
	    this.name = name;
    }

	public String getDeviceId() {
		return deviceId;
	}

	public String getAgentId() {
		return agentId;
	}

	public String getName() {
		return name;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
    public String toString() {
	    return String.format("LogDefinitionItemPath [agentId=%s, deviceId=%s, name=%s]", agentId, deviceId, name);
    }

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((agentId == null) ? 0 : agentId.hashCode());
	    result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
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
	    LogDefinitionItemPath other = (LogDefinitionItemPath) obj;
	    if (agentId == null) {
		    if (other.agentId != null)
			    return false;
	    } else if (!agentId.equals(other.agentId))
		    return false;
	    if (deviceId == null) {
		    if (other.deviceId != null)
			    return false;
	    } else if (!deviceId.equals(other.deviceId))
		    return false;
	    if (name == null) {
		    if (other.name != null)
			    return false;
	    } else if (!name.equals(other.name))
		    return false;
	    return true;
    }
}
