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

package com.cumulocity.sdk.agent.model;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

/**
 * <p>
 * Defines an agent.
 * </p><p>
 * An agent is a function that fulfills three responsibilities for a given vendor and type of devices:
 * <ul>
 * <li>It translates the device-specific interface protocol into a single reference protocol.</li>
 * <li>It transform whatever domain model the device has to a reference domain model.</li>
 * <li>It enables secure remote communication in various network architectures.</li>
 * <ul>
 * </p>
 */
public interface Agent {

	/**
	 * @return the agent ID; unique within the agent type.
	 */
	String getExternalId();
	
	/**
	 * @return the type of agent and its ID.
	 */
	String getExternalIdType();
	
	/**
	 * @return the global ID of the agent on the <tt>Cumulocity</tt> platform.
	 */
	GId getGlobalId();
	
	/**
	 * @return the representation of an agent within the <tt>Cumolocity</tt> platform.
	 */
	ManagedObjectRepresentation getAgentRepresentation();
}
