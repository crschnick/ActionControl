package org.monospark.actioncontrol.rule;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.monospark.actioncontrol.config.Config;
import org.monospark.actioncontrol.config.ConfigRegistry;
import org.monospark.actioncontrol.rule.filter.ActionFilterTemplate;
import org.monospark.actioncontrol.rule.impl.BlockBreakRule;
import org.monospark.actioncontrol.rule.impl.BlockInteractRule;
import org.monospark.actioncontrol.rule.impl.BlockPlaceRule;
import org.monospark.actioncontrol.rule.impl.CraftRule;
import org.monospark.actioncontrol.rule.impl.EntityInteractRule;
import org.monospark.actioncontrol.rule.impl.ItemUseRule;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.entity.InteractEntityEvent;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

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
        handlers.add(new BlockInteractRule());
        handlers.add(new EntityInteractRule<InteractEntityEvent.Primary>("attackEntity",
                InteractEntityEvent.Primary.class));
        handlers.add(new EntityInteractRule<InteractEntityEvent.Secondary>("interactWithEntity",
                InteractEntityEvent.Secondary.class));
        handlers.add(new ItemUseRule());
        handlers.add(new CraftRule());
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

    protected abstract boolean acceptsEvent(E event);

    protected abstract ActionFilterTemplate createFilter();

    @Override
    public final void handle(E event) throws Exception {
        if (!acceptsEvent(event)) {
            return;
        }

        Optional<Player> player = event.getCause().first(Player.class);
        if (!player.isPresent()) {
            return;
        }

        Set<Config> configs = ConfigRegistry.getRegistry().getConfigs(player.get());
        if (configs.size() == 0) {
            return;
        }

        for (Config c : configs) {
            @SuppressWarnings("unchecked")
            ActionSettings<E> settings = (ActionSettings<E>) c.getSettings().get(this);
            if (settings == null) {
                continue;
            }

            settings.handleEvent(event);
        }
    }

    public final ActionSettings<E> deserializeSettings(ConfigurationNode node) throws ObjectMappingException,
            IOException {
        return settingsDeserializer.deserialize(node);
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
