package org.monospark.actionpermissions;

public abstract class ActionSettings {

	private ActionResponse response;

	public ActionSettings(ActionResponse response) {
		this.response = response;
	}

	public ActionResponse getResponse() {
		return response;
	}
}
