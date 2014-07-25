package com.cumulocity.tixi.server.model.txml;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.cumulocity.tixi.server.components.txml.TXMLDateAdapter;

public class RecordItemSet extends LogBaseItem {

	@XmlAttribute
	@XmlJavaTypeAdapter(TXMLDateAdapter.class)
	private Date dateTime;

	@XmlElements({ @XmlElement(name = "RecordItem") })
	private List<RecordItem> recordItems = new ArrayList<>();

	public RecordItemSet() {
	}

	public RecordItemSet(String id, Date dateTime) {
		super(id);
		this.dateTime = dateTime;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public List<RecordItem> getRecordItems() {
		return recordItems;
	}

	public void setRecordItems(List<RecordItem> recordItems) {
		this.recordItems = recordItems;
	}

	@Override
	public String toString() {
		return String.format("LogItemSet [id=%s, dateTime=%s, recordItems=%s]", id, dateTime, recordItems);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateTime == null) ? 0 : dateTime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((recordItems == null) ? 0 : recordItems.hashCode());
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
		RecordItemSet other = (RecordItemSet) obj;
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
		if (recordItems == null) {
			if (other.recordItems != null)
				return false;
		} else if (!recordItems.equals(other.recordItems))
			return false;
		return true;
	}
}
