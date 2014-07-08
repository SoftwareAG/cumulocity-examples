package com.cumulocity.tixi.server.services.handler;

public interface AbstractHandler<T> {
	
	void handle(T element);

}
