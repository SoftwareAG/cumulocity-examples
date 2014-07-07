package com.cumulocity.tixi.server.model.txml.log;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DataloggingItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class LogItem {

	@XmlAttribute
	private String id;

	@XmlAttribute
	private String value;
	
	public LogItem() {}
	
	public LogItem(String id, String value) {
	    this.id = id;
	    this.value = value;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
    public String toString() {
	    return String.format("LogItem [id=%s, value=%s]", id, value);
    }

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((id == null) ? 0 : id.hashCode());
	    result = prime * result + ((value == null) ? 0 : value.hashCode());
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
	    LogItem other = (LogItem) obj;
	    if (id == null) {
		    if (other.id != null)
			    return false;
	    } else if (!id.equals(other.id))
		    return false;
	    if (value == null) {
		    if (other.value != null)
			    return false;
	    } else if (!value.equals(other.value))
		    return false;
	    return true;
    }
}
