package org.monospark.actioncontrol.handler;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.monospark.actioncontrol.category.Category;
import org.monospark.actioncontrol.handler.filter.ActionFilterTemplate;
import org.monospark.actioncontrol.handler.impl.BlockBreakHandler;
import org.monospark.actioncontrol.handler.impl.BlockInteractHandler;
import org.monospark.actioncontrol.handler.impl.BlockPlaceHandler;
import org.monospark.actioncontrol.handler.impl.EntityInteractHandler;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.CauseTracked;
import org.spongepowered.api.event.entity.InteractEntityEvent;

public abstract class ActionHandler<E extends Event & Cancellable & CauseTracked>
		implements EventListener<E> {

	public static final Set<ActionHandler<?>> ALL = createAllActionHandlers();
	
	public static Optional<ActionHandler<?>> byName(String name) {
		for (ActionHandler<?> handler : ALL) {
			if(handler.getName().equals(name)) {
				return Optional.of(handler);
			}
		}
		return Optional.empty();
	}
	
	private static final Set<ActionHandler<?>> createAllActionHandlers() {
		Set<ActionHandler<?>> handlers = new HashSet<ActionHandler<?>>();
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
		return handlers;
	}

	private String name;
	
	private Class<E> eventClass;
	
	private ActionFilterTemplate filter;

	protected ActionHandler(String name, Class<E> eventClass) {
		this.name = name;
		this.eventClass = eventClass;
		filter = createFilter();
	}
	
	protected abstract ActionFilterTemplate createFilter();

	@Override
	public final void handle(E event) throws Exception {
		Optional<Player> player = event.getCause().first(Player.class);
		if(!player.isPresent()) {
			return;
		}
		
		Set<Category> categories = Category.getRegistry().getCategories(player.get());
		if(categories.size() == 0) {
			return;
		}
		
		for(Category c : categories) {
			Optional<ActionSettings<E>> settings = c.getActionSettings(this);
			if(!settings.isPresent()) {
				continue;
			}
			
			boolean allowed = settings.get().isAllowed(event);
			if(!allowed) {
				event.setCancelled(true);
				return;
			}
		}
	}

	public String getName() {
		return name;
	}

	public Class<E> getEventClass() {
		return eventClass;
	}

	public ActionFilterTemplate getFilterTemplate() {
		return filter;
	}
}
