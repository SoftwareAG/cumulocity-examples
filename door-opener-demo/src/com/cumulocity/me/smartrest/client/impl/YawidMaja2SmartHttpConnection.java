package com.cumulocity.me.smartrest.client.impl;

import com.cumulocity.me.smartrest.client.SmartExecutorService;
import com.cumulocity.me.smartrest.client.SmartRequest;
import com.cumulocity.me.smartrest.client.SmartResponse;

public class YawidMaja2SmartHttpConnection extends SmartHttpConnection {

	private final static long SLEEPING_TIME_MILLIS = 120000L;
	private final static int MAX_REQUEST_COUNT_BEFORE_SLEEPING = 8;
	
	private static int requestCount = 0;


	public YawidMaja2SmartHttpConnection(String host, String xid, String authorization, SmartExecutorService executorService) {
		super(host, xid, authorization, executorService);
	}
	
	public YawidMaja2SmartHttpConnection(String host, String xid, String authorization) {
		super(host, xid, authorization);
	}
	
	public YawidMaja2SmartHttpConnection(String host, String tenant, String username, String password, String xid) {
		super(host, tenant, username, password, xid);
	}
	
	public YawidMaja2SmartHttpConnection(String host, String tenant, String username,
		String password, String xid, SmartExecutorService executorService) {
		super(host, tenant, username, password, xid, executorService);
	}
	
	/**
	 * Not thread-safe
	 */
	public SmartResponse executeRequest(SmartRequest request) {
		System.out.println("executeRequest in YawidMaja2SmartHttpConnection");
		SmartResponse smartResponse = null;
		
		//e.g., if MAX_REQUEST_COUNT_BEFORE_PAUSE = 4, then behavior shall be 0 1 2 3pause 4 5 6 7pause .... 
		if ( requestCount % MAX_REQUEST_COUNT_BEFORE_SLEEPING == (MAX_REQUEST_COUNT_BEFORE_SLEEPING - 1) ) {
			try {
				System.out.println("As this would be a MAX_REQUEST_COUNT_BEFORE_SLEEPING's request, falling asleep and retrying later");
				Thread.sleep(SLEEPING_TIME_MILLIS);
				System.out.println("Blocking sleeping time is over");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException("Thread was interrupted");
			}
		}
		
		requestCount++;
		smartResponse = super.executeRequest(request);
		return smartResponse;
	}


}
