package com.cumulocity.tixi.server.model.txml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Datalogging")
public class Log extends LogBaseItem {
	
	@XmlElements({ @XmlElement(name = "DataloggingItemSet") })
	private List<LogItemSet> itemSets = new ArrayList<>();

	public List<LogItemSet> getItemSets() {
		return itemSets;
	}

	public void setItemSets(List<LogItemSet> itemSets) {
		this.itemSets = itemSets;
	}

	@Override
    public String toString() {
	    return String.format("Log [id=%s, itemSets=%s]", id, itemSets);
    }

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
	    Log other = (Log) obj;
	    if (id == null) {
		    if (other.id != null)
			    return false;
	    } else if (!id.equals(other.id))
		    return false;
	    if (itemSets == null) {
		    if (other.itemSets != null)
			    return false;
	    } else if (!itemSets.equals(other.itemSets))
		    return false;
	    return true;
    }
}
