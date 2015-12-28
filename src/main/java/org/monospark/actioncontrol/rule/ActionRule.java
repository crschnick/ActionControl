package org.monospark.actioncontrol.rule;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.monospark.actioncontrol.category.Category;
import org.monospark.actioncontrol.rule.filter.ActionFilterTemplate;
import org.monospark.actioncontrol.rule.impl.BlockBreakRule;
import org.monospark.actioncontrol.rule.impl.BlockInteractRule;
import org.monospark.actioncontrol.rule.impl.BlockPlaceRule;
import org.monospark.actioncontrol.rule.impl.EntityInteractRule;
import org.monospark.actioncontrol.rule.impl.UseItemRule;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;

import com.google.gson.JsonElement;

public abstract class ActionRule<E extends Event & Cancellable>
        implements EventListener<E> {

    public static final Set<ActionRule<?>> ALL = createAllActionRules();

    public static Optional<ActionRule<?>> byName(String name) {
        for (ActionRule<?> handler : ALL) {
            if (handler.getName().equals(name)) {
                return Optional.of(handler);
            }
        }
        return Optional.empty();
    }

    private static Set<ActionRule<?>> createAllActionRules() {
        Set<ActionRule<?>> handlers = new HashSet<ActionRule<?>>();
        handlers.add(new BlockPlaceRule());
        handlers.add(new BlockBreakRule());
        handlers.add(new BlockInteractRule<InteractBlockEvent.Primary>("leftClickBlock",
                InteractBlockEvent.Primary.class));
        handlers.add(new BlockInteractRule<InteractBlockEvent.Secondary>("rightClickBlock",
                InteractBlockEvent.Secondary.class));
        handlers.add(new EntityInteractRule<InteractEntityEvent.Primary>("leftClickEntity",
                InteractEntityEvent.Primary.class));
        handlers.add(new EntityInteractRule<InteractEntityEvent.Secondary>("rightClickEntity",
                InteractEntityEvent.Secondary.class));
        handlers.add(new UseItemRule());
        return handlers;
    }

    private String name;

    private Class<E> eventClass;

    private ActionFilterTemplate filter;

    private ActionSettings.Deserializer<E> settingsDeserializer;

    protected ActionRule(String name, Class<E> eventClass) {
        this.name = name;
        this.eventClass = eventClass;
        filter = createFilter();
        settingsDeserializer = new ActionSettings.Deserializer<>(this);
    }

    protected abstract ActionFilterTemplate createFilter();

    @Override
    public final void handle(E event) throws Exception {
        Optional<Player> player = event.getCause().first(Player.class);
        if (!player.isPresent()) {
            return;
        }

        Set<Category> categories = Category.getRegistry().getCategories(player.get());
        if (categories.size() == 0) {
            return;
        }

        for (Category c : categories) {
            Optional<ActionSettings<E>> settings = c.getActionSettings(this);
            if (!settings.isPresent()) {
                continue;
            }

            boolean allowed = settings.get().isAllowed(event);
            if (!allowed) {
                event.setCancelled(true);
                return;
            }
        }
    }

    public final ActionSettings<E> deserializeSettings(JsonElement json) {
        return settingsDeserializer.deserialize(json);
    }

    public final String getName() {
        return name;
    }

    public final Class<E> getEventClass() {
        return eventClass;
    }

    public final ActionFilterTemplate getFilterTemplate() {
        return filter;
    }
}
