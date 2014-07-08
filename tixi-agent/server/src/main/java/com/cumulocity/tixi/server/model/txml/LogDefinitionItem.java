package com.cumulocity.tixi.server.model.txml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.cumulocity.tixi.server.components.txml.LogDefinitionItemPathAdapter;

@XmlRootElement(name = "LogDefinitionItem")
public class LogDefinitionItem extends LogBaseItem {
	
	@XmlAttribute(name = "Name")
	private String name;

	@XmlAttribute(name = "_")
	private String type;

	@XmlAttribute
	private int size;

	@XmlAttribute
	private int exp;

	@XmlAttribute
	private String format;
	
	@XmlAttribute
	@XmlJavaTypeAdapter(value = LogDefinitionItemPathAdapter.class)
	private LogDefinitionItemPath path;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public LogDefinitionItemPath getPath() {
		return path;
	}

	public void setPath(LogDefinitionItemPath path) {
		this.path = path;
	}

	@Override
    public String toString() {
	    return String.format("DataLoggingItem [id=%s, name=%s, type=%s, size=%s, exp=%s, format=%s, path=%s]", id, name, type, size, exp, format, path);
    }

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + exp;
	    result = prime * result + ((format == null) ? 0 : format.hashCode());
	    result = prime * result + ((id == null) ? 0 : id.hashCode());
	    result = prime * result + ((name == null) ? 0 : name.hashCode());
	    result = prime * result + ((path == null) ? 0 : path.hashCode());
	    result = prime * result + size;
	    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
	    LogDefinitionItem other = (LogDefinitionItem) obj;
	    if (exp != other.exp)
		    return false;
	    if (format == null) {
		    if (other.format != null)
			    return false;
	    } else if (!format.equals(other.format))
		    return false;
	    if (id == null) {
		    if (other.id != null)
			    return false;
	    } else if (!id.equals(other.id))
		    return false;
	    if (name == null) {
		    if (other.name != null)
			    return false;
	    } else if (!name.equals(other.name))
		    return false;
	    if (path == null) {
		    if (other.path != null)
			    return false;
	    } else if (!path.equals(other.path))
		    return false;
	    if (size != other.size)
		    return false;
	    if (type == null) {
		    if (other.type != null)
			    return false;
	    } else if (!type.equals(other.type))
		    return false;
	    return true;
    }

}
