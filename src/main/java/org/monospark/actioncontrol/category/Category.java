package org.monospark.actioncontrol.category;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.monospark.actioncontrol.rule.ActionRule;
import org.monospark.actioncontrol.rule.ActionSettings;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

public final class Category {

    private static final CategoryRegistry REGISTRY = new CategoryRegistry();

    public static CategoryRegistry getRegistry() {
        return REGISTRY;
    }

    private String name;

    private MatchType matchType;

    private Map<ActionRule<?>, ActionSettings<?>> settings;

    public Category(String name, MatchType matchType, Map<ActionRule<?>, ActionSettings<?>> settings) {
        this.name = name;
        this.matchType = matchType;
        this.settings = settings;
    }

    public String getName() {
        return name;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    @SuppressWarnings("unchecked")
    public <E extends Event & Cancellable> Optional<ActionSettings<E>> getActionSettings(ActionRule<E> rule) {
        return Optional.ofNullable((ActionSettings<E>) settings.get(rule));
    }

    public enum MatchType {

        SIMPLE(b -> b),
        EXCEPT(b -> !b);

        private Function<Boolean, Boolean> matchFunction;

        MatchType(Function<Boolean, Boolean> matchFunction) {
            this.matchFunction = matchFunction;
        }

        public Function<Boolean, Boolean> getMatchFunction() {
            return matchFunction;
        }
    }
}
