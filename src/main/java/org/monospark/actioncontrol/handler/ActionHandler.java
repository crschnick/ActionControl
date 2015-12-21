package org.monospark.actioncontrol.handler;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.monospark.actioncontrol.handler.impl.BlockBreakHandler;
import org.monospark.actioncontrol.handler.impl.BlockInteractHandler;
import org.monospark.actioncontrol.handler.impl.BlockPlaceHandler;
import org.monospark.actioncontrol.handler.impl.EntityInteractHandler;
import org.monospark.actioncontrol.handler.impl.UseItemHandler;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;

import com.google.gson.JsonElement;

public abstract class ActionHandler<E extends Event & Cancellable, S extends ActionSettings>
		implements EventListener<E> {

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
		handlers.add(new BlockPlaceHandler());
		handlers.add(new BlockBreakHandler());
		handlers.add(new BlockInteractHandler<InteractBlockEvent.Primary>("leftClickBlock",
				InteractBlockEvent.Primary.class));
		handlers.add(new BlockInteractHandler<InteractBlockEvent.Secondary>("rightClickBlock",
				InteractBlockEvent.Secondary.class));
		handlers.add(new EntityInteractHandler<InteractEntityEvent.Primary>("leftClickEntity",
				InteractEntityEvent.Primary.class));
		handlers.add(new EntityInteractHandler<InteractEntityEvent.Secondary>("rightClickEntity",
				InteractEntityEvent.Secondary.class));
		handlers.add(new UseItemHandler());
		return handlers;
	}

	private String name;
	
	private Class<E> eventClass;

	protected ActionHandler(String name, Class<E> eventClass) {
		this.name = name;
		this.eventClass = eventClass;
	}
	
	public abstract S deserializeSettings(JsonElement json);

	public String getName() {
		return name;
	}

	public Class<E> getEventClass() {
		return eventClass;
	}
}
