package com.cumulocity.tixi.server.model.txml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Log")
public class Log extends LogBaseItem {
	
	@XmlElements({ @XmlElement(name = "RecordItemSet") })
	private List<RecordItemSet> recordItemSets = new ArrayList<>();

	public List<RecordItemSet> getRecordItemSets() {
		return recordItemSets;
	}

	public void setRecordItemSets(List<RecordItemSet> recordItemSets) {
		this.recordItemSets = recordItemSets;
	}

	@Override
    public String toString() {
	    return String.format("Log [recordName=%s, recordItemSets=%s]", id, recordItemSets);
    }

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((id == null) ? 0 : id.hashCode());
	    result = prime * result + ((recordItemSets == null) ? 0 : recordItemSets.hashCode());
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
	    if (recordItemSets == null) {
		    if (other.recordItemSets != null)
			    return false;
	    } else if (!recordItemSets.equals(other.recordItemSets))
		    return false;
	    return true;
    }
}
