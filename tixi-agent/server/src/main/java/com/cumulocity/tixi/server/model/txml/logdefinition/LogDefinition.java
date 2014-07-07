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
	@XmlElements({ @XmlElement(name = "Datalogging", type = LogDefinitionItemSet.class) })
	private List<LogDefinitionItemSet> itemSets = new ArrayList<>();

	public List<LogDefinitionItemSet> getItemSets() {
		return itemSets;
	}

	public void setItemSets(List<LogDefinitionItemSet> dataLoggings) {
		this.itemSets = dataLoggings;
	}
	
	@Override
    public String toString() {
	    return String.format("LogDefinition [itemSets=%s]", itemSets);
    }

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((itemSets == null) ? 0 : itemSets.hashCode());
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
	    if (itemSets == null) {
		    if (other.itemSets != null)
			    return false;
	    } else if (!itemSets.equals(other.itemSets))
		    return false;
	    return true;
    }
}
