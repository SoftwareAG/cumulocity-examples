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
package c8y.lx.driver;

import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;

/**
 * <p>
 * A driver provides access to certain hardware capabilities of a device. It
 * translates hardware-specific properties into Cumulocity sensor library
 * concepts in terms of inventory/device topology, readings, events asf. It
 * executes standard Cumulocity operations.
 * </p>
 * <p>
 * The driver's methods are executed in the following order: initialize,
 * initializeInventory, discoverChildren, start.
 * </p>
 * <p>
 * If the hardware associated with a driver is not present, it may log that fact
 * but should otherwise silently ignore it (e.g., it should not add or remove
 * inventory items).
 * </p>
 * <p>
 * Drivers can rely on the presence of an initialized configuration fragment in
 * the inventory, if there is a configuration possibility for the device.
 * </p>
 * <p>
 * Drivers can log messages using SLF4J Loggers. Log levels:</p>
 * <ul>
 * <li>Error: Serious error requiring human intervention. Sent as alarm to the platform.</li>
 * <li>Warn: Error within driver, agent can continue to operation. Sent as event to the platform.</li>
 * <li>Info: Low volume informational messages. Sent as event if trace is enabled.</li>
 * <li>Debug: Higher volume informational messages. Sent as event if trace is enabled.</li>
 * <li>Trace: High volume including stack traces. Not sent unless requested.</li>
 * </ul>
 */
public interface Driver {
    
    /**
     * Sets up driver. Errors during
     * setup are reported as exception and send to the log.
     */
    void initialize() throws Exception;
    
    /**
     * Initialize platform connectivity.
     */
    void initialize(Platform platform) throws Exception;

    /**
     * Return the operations supported by this driver.
     */
    OperationExecutor[] getSupportedOperations();

    /**
     * Provides additional fragments for the managed object representing the
     * device in the inventory. Note: The managed object's representation may
     * not have a global ID at this stage. 
     * 
     */
    void initializeInventory(ManagedObjectRepresentation mo);

    /**
     * Synchronizes the children of the managed object in the inventory. At this
     * point in time, the representation has a global ID Note that the driver
     * may only manipulate types of children that it recognizes.
     */
    void discoverChildren(ManagedObjectRepresentation mo);

    /**
     * Start the regular tasks of this driver, i.e., listeners for sensors
     * events or sensor polling.
     */
    void start();
}
