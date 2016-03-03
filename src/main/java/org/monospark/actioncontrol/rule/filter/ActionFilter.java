package org.monospark.actioncontrol.rule.filter;

import java.util.Map;
import java.util.Map.Entry;

import org.monospark.spongematchers.matcher.SpongeMatcher;
import org.spongepowered.api.event.Event;

public final class ActionFilter<E extends Event> {

    private Map<ActionFilterOption<?, E>, SpongeMatcher<?>> optionMatchers;

    public ActionFilter(Map<ActionFilterOption<?, E>, SpongeMatcher<?>> optionMatchers) {
        this.optionMatchers = optionMatchers;
    }

    public boolean matches(E event) {
        boolean allMatch = true;

        for (Entry<ActionFilterOption<?, E>, SpongeMatcher<?>> entry : optionMatchers.entrySet()) {
            Object toMatch = entry.getKey().getFunction().apply(event);
            boolean matches = matches(entry.getValue(), toMatch);
            if (!matches) {
                allMatch = false;
                break;
            }
        }

        return allMatch;
    }

    @SuppressWarnings("unchecked")
    private <T> boolean matches(SpongeMatcher<T> matcher, Object toMatch) {
        return matcher.matches((T) toMatch);
    }
}
