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

import com.cumulocity.rest.representation.operation.OperationRepresentation;

/**
 * Executes a remote control operation.
 */
public interface OperationExecutor {

    /**
     * The type of remote control operation that this OperationExecutor can execute.
     */
    String supportedOperationType();

    /**
     * Execute a particular remote control operation and write the result of the
     * operation back into the operation. Carries out additional updates, e.g.,
     * to the inventory.
     *
     * @param operation The operation to execute
     * @param cleanup   If set to true, the operation was hanging in executing state
     *                  when the agent was started. This can have multiple reasons:
     *                  One reason is that there was a failure during first execution.
     *                  In this case, cleanup may be needed. Another reason might be
     *                  that the operation required a restart of the agent, and the
     *                  operation is successful when the agent could be restarted.
     */
    void execute(OperationRepresentation operation, boolean cleanup) throws Exception;
}
