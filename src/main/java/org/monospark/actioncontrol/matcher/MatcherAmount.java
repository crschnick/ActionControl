package org.monospark.actioncontrol.matcher;

import java.util.Set;

public final class MatcherAmount<T> implements Matcher<T> {

    private Set<Matcher<T>> matchers;

    public MatcherAmount(Set<Matcher<T>> matchers) {
        this.matchers = matchers;
    }

    @Override
    public boolean matches(T o) {
        for (Matcher<T> m : matchers) {
            if (m.matches(o)) {
                return true;
            }
        }
        return false;
    }
}
