package com.cumulocity.tixi.server.model.txml;

public class DeviceVariablePath extends RecordItemPath {

    private String agentId;
    private String deviceId;
    
    public DeviceVariablePath() {}

    public DeviceVariablePath(String agentId, String deviceId, String name) {
        super(name);
        this.agentId = agentId;
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @Override
    public String toString() {
        return String.format("LogDefinitionItemPath [agentId=%s, deviceId=%s, name=%s]", agentId, deviceId, name);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((agentId == null) ? 0 : agentId.hashCode());
        result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        DeviceVariablePath other = (DeviceVariablePath) obj;
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
        return true;
    }
}
