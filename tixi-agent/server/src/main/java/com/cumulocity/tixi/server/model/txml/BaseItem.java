package com.cumulocity.tixi.server.model.txml;

import javax.xml.bind.annotation.XmlAttribute;

public abstract class BaseItem {

	@XmlAttribute
	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
