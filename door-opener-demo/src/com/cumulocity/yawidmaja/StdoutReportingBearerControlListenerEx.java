package com.cumulocity.yawidmaja;

import com.cinterion.io.BearerControlListenerEx;

public class StdoutReportingBearerControlListenerEx implements
		BearerControlListenerEx {

	public void stateChanged(String APN, int state, int pdpErrCause) {
		String newStateInformation = APN + " " + state + " " + pdpErrCause;
		System.out.println("bearer changed state: " + newStateInformation);
		switch(state) {
		case BEARER_STATE_CONNECTING:
			System.out.println("bearer connecting...");
			break;
		case BEARER_STATE_LIMITED_UP:
			System.out.println("bearer limited up...");
			break;
		case BEARER_STATE_UP:
			System.out.println("bearer up...");
			break;
		case BEARER_STATE_CLOSING:
			System.out.println("bearer closing...");
			break;
		case BEARER_STATE_DOWN:
			System.out.println("bearer down...");
			break;
		case BEARER_STATE_UNKNOWN:
			System.out.println("bearer unkown...");
			break;
		default:
			System.out.println("unknown state");
			break;
		}
		
	}

}
