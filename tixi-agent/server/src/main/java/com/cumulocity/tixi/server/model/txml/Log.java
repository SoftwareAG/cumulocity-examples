package com.cumulocity.tixi.server.model.txml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Log")
public class Log extends LogBaseItem {
	
	@XmlElements({ @XmlElement(name = "Record") })
	private List<Record> records = new ArrayList<>();

	public List<Record> getRecords() {
		return records;
	}

	public void setRecords(List<Record> records) {
		this.records = records;
	}

	@Override
    public String toString() {
	    return String.format("Log [id=%s, records=%s]", id, records);
    }

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((id == null) ? 0 : id.hashCode());
	    result = prime * result + ((records == null) ? 0 : records.hashCode());
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
	    if (records == null) {
		    if (other.records != null)
			    return false;
	    } else if (!records.equals(other.records))
		    return false;
	    return true;
    }
}
