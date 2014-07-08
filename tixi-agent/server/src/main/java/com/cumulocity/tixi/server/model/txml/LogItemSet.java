package com.cumulocity.tixi.server.model.txml;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.cumulocity.tixi.server.components.txml.TXMLDateAdapter;

@XmlRootElement(name = "DataloggingItemSet")
public class LogItemSet extends LogBaseItem {

	@XmlAttribute
	@XmlJavaTypeAdapter(TXMLDateAdapter.class)
	private Date dateTime;

	@XmlElements({ @XmlElement(name = "DataloggingItem") })
	private List<LogItem> items = new ArrayList<>();

	public LogItemSet() {
	}

	public LogItemSet(String id, Date dateTime) {
		super(id);
		this.dateTime = dateTime;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public List<LogItem> getItems() {
		return items;
	}

	public void setItems(List<LogItem> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return String.format("LogItemSet [id=%s, dateTime=%s, items=%s]", id, dateTime, items);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateTime == null) ? 0 : dateTime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
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
		LogItemSet other = (LogItemSet) obj;
		if (dateTime == null) {
			if (other.dateTime != null)
				return false;
		} else if (!dateTime.equals(other.dateTime))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return true;
	}
}
