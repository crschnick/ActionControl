package org.monospark.actioncontrol.handler;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.monospark.actioncontrol.handler.blockbreak.BlockBreakHandler;
import org.monospark.actioncontrol.handler.blockplace.BlockPlaceHandler;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;

import com.google.gson.GsonBuilder;

public abstract class ActionHandler<T extends Event, M extends ActionMatcher> implements EventListener<T> {

	public static final Set<ActionHandler<?, ?>> ALL = createAllActionHandlers();
	
	public static Optional<ActionHandler<?, ?>> byName(String name) {
		for (ActionHandler<?, ?> handler : ALL) {
			if(handler.getName().equals(name)) {
				return Optional.of(handler);
			}
		}
		return Optional.empty();
	}
	
	private static final Set<ActionHandler<?, ?>> createAllActionHandlers() {
		Set<ActionHandler<?, ?>> handlers = new HashSet<ActionHandler<?, ?>>();
//		handlers.add(new BlockPlaceHandler());
		handlers.add(new BlockBreakHandler());
//		handlers.add(new CraftHandler());
		return handlers;
	}

	private String name;
	
	private Class<M> matcherClass;
	
	private Class<T> eventClass;
	
	protected ActionHandler(String name, Class<M> matcherClass, Class<T> eventClass) {
		this.name = name;
		this.matcherClass = matcherClass;
		this.eventClass = eventClass;
	}

	public abstract M uniteMatchers(M m1, M m2);

	public abstract void registerMatcherDeserializers(GsonBuilder builder);

	public String getName() {
		return name;
	}

	public Class<M> getMatcherClass() {
		return matcherClass;
	}

	public Class<T> getEventClass() {
		return eventClass;
	}
}
