package org.monospark.actioncontrol.handler;

import java.util.Set;

import org.monospark.actioncontrol.handler.filter.ActionFilter;
import org.spongepowered.api.event.Event;

public final class ActionSettings<E extends Event> {

	private ActionResponse response;
	
	private Set<ActionFilter<E>> filters;

	public ActionSettings(ActionResponse response, Set<ActionFilter<E>> filters) {
		this.response = response;
		this.filters = filters;
	}
	
	public boolean isAllowed(E event) {
		boolean matchOccured = false;
		
		for (ActionFilter<E> filter : filters) {
			boolean matches = filter.matches(event);
			if(matches) {
				matchOccured = true;
				break;
			}
		}
		
		return matchOccured ? response == ActionResponse.ALLOW : response == ActionResponse.DENY;
	}
}
