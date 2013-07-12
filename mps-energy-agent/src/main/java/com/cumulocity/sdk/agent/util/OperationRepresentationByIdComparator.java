/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.cumulocity.sdk.agent.util;

import java.util.Comparator;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.operation.OperationRepresentation;

/**
 * A comparator for {@link OperationRepresentation} that considers two operations equal if their ID's are equal.
 */
public class OperationRepresentationByIdComparator implements Comparator<OperationRepresentation> {

	private static final Comparator<OperationRepresentation> INSTANCE =
			new OperationRepresentationByIdComparator();
	
	public static Comparator<OperationRepresentation> getInstance() {
		return INSTANCE;
	}
	
	protected OperationRepresentationByIdComparator() {
		// hidden constructor of this state-less object enforces using above singleton instance
	}
	
	@Override
	public int compare(OperationRepresentation o1, OperationRepresentation o2) {
		if (o1 == null) {
			return o2 == null ? 0 : -1;
		}
		if (o2 == null) {
			return o1 == null ? 0 : 1;
		}
		GId id1 = o1.getId();
		GId id2 = o2.getId();
		if (id1 == null) {
			return id2 == null ? 0 : -1;
		}
		if (id2 == null) {
			return id1 == null ? 0 : 1;
		}
		return id1.equals(id2) ? 0 : -1;
	}
}
