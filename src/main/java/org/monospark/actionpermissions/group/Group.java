package org.monospark.actionpermissions.group;

import java.util.Map;

import org.monospark.actionpermissions.handler.ActionHandler;
import org.monospark.actionpermissions.handler.ActionSettings;

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
	public <S extends ActionSettings> S getActionSettings(ActionHandler<?, S> handler) {
		return (S) handlers.get(handler);
	}
}
