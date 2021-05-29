/*
 * Copyright (c) 2012-2020 Cumulocity GmbH
 * Copyright (c) 2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA,
 * and/or its subsidiaries and/or its affiliates and/or their licensors.
 *
 * Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided
 * for in your License Agreement with Software AG.
 */

package c8y.trackeragent.service;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;

import c8y.trackeragent.context.ReportContext;

public interface AlarmType {
    
    Object[] getTextArgs(AlarmRepresentation alarm, ReportContext reportContext);
    
    String name();

}
