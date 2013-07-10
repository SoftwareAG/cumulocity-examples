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

import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

/**
 * Controls the agent execution.
 */
public class AgentActionsController {

    private static final Logger LOG = LoggerFactory.getLogger(AgentActionsController.class);

    private TaskScheduler scheduler;
    
    private AgentActionsChain startupChain;
    
    private AgentActionsChain cyclicChain;
    
    private int cyclicChainDelay;
    
    private ScheduledFuture<?> scheduledCyclicChain;

    /**
     * @param scheduler the scheduler to use to schedule the cyclic chain. 
     */
    @Autowired
    public AgentActionsController(TaskScheduler scheduler) {
		this.scheduler = scheduler;
	}

	/**
     * Sets the chain of actions to run on agent startup.
     * @param startupChain the chain of startup actions.
     */
    public void setStartupChain(AgentActionsChain startupChain) {
		this.startupChain = startupChain;
	}
    
    /**
     * Sets the chain of actions to run on periodically on fixed delay.
     * @param cyclicChain the chain of periodical actions.
     */
    public void setCyclicChain(AgentActionsChain cyclicChain) {
		this.cyclicChain = cyclicChain;
	}
    
    /**
     * Sets the delay between each execution of periodical actions chain.
     * @param cyclicChainDelay the delay between executions.
     */
    public void setCyclicChainDelay(int cyclicChainDelay) {
		this.cyclicChainDelay = cyclicChainDelay;
	}
    
    /**
     * Returns the number of times the startup actions chain was executed. Delegates to the chain itself to get the result.
     * @return <tt>0</tt> until the startup actions chain was completed, <tt>1</tt> afterwards.
     */
    public int startupChainNumOfExecutions() {
		return startupChain.getNumOfExecutions();
	}
    
    /**
     * Returns the number of times the cyclic actions chain was executed. Delegates to the chain itself to get the result.
     * @return the number of executions of the cyclic actions chain.
     */
    public int cyclicChainNumOfExecutions() {
		return cyclicChain.getNumOfExecutions();
	}

    /**
     * Starts the agent.
     */
    @PostConstruct
    public void start() {
    	LOG.info("Starting agent...");
    	
    	startupChain.run();
    	
    	scheduledCyclicChain = scheduler.scheduleWithFixedDelay(cyclicChain, cyclicChainDelay);
    	
    	LOG.info("Started agent.");
    }
    
    /**
     * Stops the agent.
     */
    @PreDestroy
    public void stop() {
    	scheduledCyclicChain.cancel(true);
    }
}
