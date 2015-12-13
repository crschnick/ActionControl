package org.monospark.actionpermissions.handler;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.monospark.actionpermissions.handler.blockbreak.BlockBreakHandler;
import org.monospark.actionpermissions.handler.blockplace.BlockPlaceHandler;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;

import com.google.gson.JsonDeserializer;

public abstract class ActionHandler<T extends Event, S extends ActionSettings> implements EventListener<T> {

	public static final Set<ActionHandler<?,?>> ALL = createAllActionHandlers();
	
	public static Optional<ActionHandler<?, ?>> byName(String name) {
		for (ActionHandler<?, ?> handler : ALL) {
			if(handler.getName().equals(name)) {
				return Optional.of(handler);
			}
		}
		return Optional.empty();
	}
	
	private static final Set<ActionHandler<?,?>> createAllActionHandlers() {
		Set<ActionHandler<?,?>> handlers = new HashSet<ActionHandler<?,?>>();
		handlers.add(new BlockPlaceHandler());
		handlers.add(new BlockBreakHandler());
//		handlers.add(new CraftHandler());
		return handlers;
	}

	private String name;
	
	private Class<S> settingsClass;
	
	private Class<T> eventClass;
	
	protected ActionHandler(String name, Class<S> settingsClass, Class<T> eventClass) {
		this.name = name;
		this.settingsClass = settingsClass;
		this.eventClass = eventClass;
	}

	public abstract S uniteSettings(S s1, S s2);
	
	public abstract JsonDeserializer<S> getSettingsDeserializer();

	public String getName() {
		return name;
	}

	public Class<S> getSettingsClass() {
		return settingsClass;
	}

	public Class<T> getEventClass() {
		return eventClass;
	}
}
