package com.cumulocity.agent.snmp.repository;

import java.io.Serializable;
import java.util.Optional;

public interface DataStore {

	void store(Serializable value);

	<T extends Serializable> Optional<T> get();

	void remove();
}