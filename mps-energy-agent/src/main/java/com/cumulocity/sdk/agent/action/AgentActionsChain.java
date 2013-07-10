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

package com.cumulocity.sdk.agent.action;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a chain of {@link AgentAction} that are executed sequentially as a batch.
 */
public class AgentActionsChain implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(AgentActionsChain.class);
	
	private volatile int numOfExecutions = 0;
	
	private List<AgentAction> actions;
	
	/**
	 * Sets the actions to be executed.
	 * @param actions the actions.
	 */
	public void setActions(List<AgentAction> actions) {
		this.actions = actions;
	}
	
	/**
	 * Gets the number of times the chain was fully executed.
	 * @return the number of full executions.
	 */
	public int getNumOfExecutions() {
		return numOfExecutions;
	}
	
	@Override
	public void run() {
		LOG.info(String.format("Starting execution #%d of chain of %d actions...", numOfExecutions + 1, actions.size()));
		try {
			for (AgentAction action : actions) {
				action.run();
				LOG.info(String.format("Action %s run successfully.", action.getClass().getName()));
			}
		} finally {
			numOfExecutions++;
		}
		LOG.info(String.format("Execution #%d of chain of %d actions ended.", numOfExecutions, actions.size()));
	}
}
