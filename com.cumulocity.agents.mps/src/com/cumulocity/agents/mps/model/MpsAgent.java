package com.cumulocity.agents.mps.model;

import com.cumulocity.sdk.agent.model.AbstractDevicesManagingAgent;

/**
 * The agent managing MPS devices.
 */
public class MpsAgent extends AbstractDevicesManagingAgent<MpsDevice> {
	
	public static final String EXTERNAL_ID_TYPE = "com_cumulocity_agents_mps_model_MpsAgent";

	private String externalId;

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	
	@Override
	public String getExternalId() {
		return externalId;
	}

	@Override
	public String getExternalIdType() {
		return EXTERNAL_ID_TYPE;
	}
}
