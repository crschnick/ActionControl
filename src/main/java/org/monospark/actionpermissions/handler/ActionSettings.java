package org.monospark.actionpermissions.handler;

public abstract class ActionSettings {

	private ActionResponse response;

	public ActionSettings(ActionResponse response) {
		this.response = response;
	}

	public ActionResponse getResponse() {
		return response;
	}
}