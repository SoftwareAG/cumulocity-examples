package com.cumulocity.yawidmaja;

import com.cumulocity.me.smartrest.client.SmartResponse;
import com.cumulocity.me.smartrest.client.SmartResponseEvaluator;
import com.cumulocity.me.smartrest.client.impl.SmartRow;

public class StdoutReportingSmartResponseEvaluator implements
		SmartResponseEvaluator {

	public void evaluate(SmartResponse smartResponse) {
		System.out.println("Status: " + smartResponse.getStatus() );
		System.out.println("Message: " + smartResponse.getMessage() );
		
		SmartRow[] smartRows = smartResponse.getDataRows();
		for (int i = 0; i < smartRows.length; i++) {
			System.out.println("----------------");
			SmartRow smartRow = smartRows[i];
			System.out.println("SmartRow number " + i + " / SmartRow number " + smartRow.getRowNumber() );
			
			System.out.println("messageId: " + smartRow.getMessageId() );
			String[] dataArray = smartRow.getData();
			for (int j = 0; j < dataArray.length; j++) {
				System.out.println("dataArray[" + j + "]: " + dataArray[j] );
			}
			
			System.out.println("----------------");
		}
		
	}

}
