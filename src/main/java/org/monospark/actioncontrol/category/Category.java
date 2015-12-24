package org.monospark.actioncontrol.category;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.monospark.actioncontrol.handler.ActionHandler;
import org.monospark.actioncontrol.handler.ActionSettings;

public final class Category {

    private static final CategoryRegistry REGISTRY = new CategoryRegistry();

    public static CategoryRegistry getRegistry() {
        return REGISTRY;
    }

    private String name;

    private MatchType matchType;

    private Map<ActionHandler<?, ?>, ActionSettings> settings;

    public Category(String name, MatchType matchType, Map<ActionHandler<?, ?>, ActionSettings> settings) {
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
    public <S extends ActionSettings> Optional<S> getActionSettings(ActionHandler<?, S> handler) {
        return Optional.ofNullable((S) settings.get(handler));
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
