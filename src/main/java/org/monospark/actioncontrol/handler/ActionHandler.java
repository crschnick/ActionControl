package org.monospark.actioncontrol.handler;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.monospark.actioncontrol.category.Category;
import org.monospark.actioncontrol.handler.blockbreak.BlockBreakHandler;
import org.monospark.actioncontrol.handler.blockbreak.BlockBreakMatcher;
import org.monospark.actioncontrol.handler.blockinteract.BlockInteractHandler;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.cause.CauseTracked;

import com.google.gson.GsonBuilder;

public abstract class ActionHandler<T extends Event & Cancellable & CauseTracked, M extends ActionMatcher>
		implements EventListener<T> {

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
		handlers.add(new BlockInteractHandler());
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

	@Override
	public final void handle(T event) throws Exception {
		Optional<Player> player = event.getCause().first(Player.class);
		if(!player.isPresent()) {
			return;
		}

		Set<Category> categories = Category.getRegistry().getCategories(player.get());
		if(categories.size() == 0) {
			return;
		}
		
		for(Category c : categories) {
			Optional<ActionSettings<M>> settings = c.getActionSettings(this);
			if(!settings.isPresent()) {
				continue;
			}
			
			boolean matches = matches(event, player.get(), settings.get().getMatcher());
			boolean allowed = settings.get().isAllowed(matches);
			if(!allowed) {
				event.setCancelled(true);
				return;
			}
		}
	}

	protected abstract boolean matches(T event, Player p, M matcher);

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
