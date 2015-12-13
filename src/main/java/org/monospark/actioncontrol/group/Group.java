package org.monospark.actioncontrol.group;

import java.util.Map;
import java.util.Optional;

import org.monospark.actioncontrol.handler.ActionHandler;
import org.monospark.actioncontrol.handler.ActionSettings;

public final class Group {
	
	private static final GroupRegistry REGISTRY = new GroupRegistry();

	public static GroupRegistry getRegistry() {
		return REGISTRY;
	}

	private String name;

	private Map<ActionHandler<?,?>, ActionSettings> handlers;

	public Group(String name, Map<ActionHandler<?, ?>, ActionSettings> handlers) {
		this.name = name;
		this.handlers = handlers;
	}
	
	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	public <S extends ActionSettings> Optional<S> getActionSettings(ActionHandler<?, S> handler) {
		return Optional.ofNullable((S) handlers.get(handler));
	}
}
