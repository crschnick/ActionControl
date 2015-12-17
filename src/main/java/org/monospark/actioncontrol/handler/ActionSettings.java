package org.monospark.actioncontrol.handler;

public final class ActionSettings<M extends ActionMatcher> {

	private ActionResponse response;
	
	private M matcher;

	public ActionSettings(ActionResponse response, M matcher) {
		this.response = response;
		this.matcher = matcher;
	}
	
	public boolean isAllowed(boolean matches) {
		if(matches) {
			return response == ActionResponse.ALLOW;
		} else {
			return response == ActionResponse.DENY;
		}
	}
	
	public ActionResponse getResponse() {
		return response;
	}

	public M getMatcher() {
		return matcher;
	}
}
