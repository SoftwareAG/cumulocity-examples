/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.migration.model;

public class MigrationException extends RuntimeException {

	private static final long serialVersionUID = 1845758188692248140L;

	public MigrationException() {
		super();
	}

	public MigrationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MigrationException(String message, Throwable cause) {
		super(message, cause);
	}

	public MigrationException(String message) {
		super(message);
	}

	public MigrationException(Throwable cause) {
		super(cause);
	}

}
