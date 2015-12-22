package org.monospark.actioncontrol.category;

import java.util.Map;
import java.util.Optional;

import org.monospark.actioncontrol.handler.ActionHandler;
import org.monospark.actioncontrol.handler.ActionSettings;

public final class Category {

    private static final CategoryRegistry REGISTRY = new CategoryRegistry();

    public static CategoryRegistry getRegistry() {
        return REGISTRY;
    }

    private String name;

    private Map<ActionHandler<?, ?>, ActionSettings> settings;

    public Category(String name, Map<ActionHandler<?, ?>, ActionSettings> settings) {
        this.name = name;
        this.settings = settings;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("unchecked")
    public <S extends ActionSettings> Optional<S> getActionSettings(ActionHandler<?, S> handler) {
        return Optional.ofNullable((S) settings.get(handler));
    }
}
