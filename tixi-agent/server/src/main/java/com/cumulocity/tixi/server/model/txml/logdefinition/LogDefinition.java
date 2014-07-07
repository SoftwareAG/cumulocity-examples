package com.cumulocity.tixi.server.model.txml.logdefinition;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "LogDefinition")
@XmlAccessorType(XmlAccessType.FIELD)
public class LogDefinition {

	@XmlElementWrapper(name = "Records")
	@XmlElements({ @XmlElement(name = "Datalogging", type = DataLogging.class) })
	private List<DataLogging> dataLoggings = new ArrayList<>();

	public List<DataLogging> getDataLoggings() {
		return dataLoggings;
	}

	public void setDataLoggings(List<DataLogging> dataLoggings) {
		this.dataLoggings = dataLoggings;
	}
	
	@Override
    public String toString() {
	    return String.format("LogDefinition [dataLoggings=%s]", dataLoggings);
    }

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((dataLoggings == null) ? 0 : dataLoggings.hashCode());
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
	    LogDefinition other = (LogDefinition) obj;
	    if (dataLoggings == null) {
		    if (other.dataLoggings != null)
			    return false;
	    } else if (!dataLoggings.equals(other.dataLoggings))
		    return false;
	    return true;
    }
}
