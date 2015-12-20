package org.monospark.actioncontrol.matcher;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Maps;

public abstract class MatcherCreator<T> {

	private Matcher<T> wildcard;
	
	private Map<String, Matcher<T>> customMatchers;

	protected MatcherCreator() {
		customMatchers = Maps.newHashMap();
		addCustomMatchers(customMatchers);
		wildcard = createWildcardMatcher();
		customMatchers.put("*", wildcard);
	}
	
	protected abstract Matcher<T> createWildcardMatcher();
	
	protected void addCustomMatchers(Map<String, Matcher<T>> matchers) {}
	
	public final Optional<? extends Matcher<T>> getMatcher(String name) {
		Matcher<T> matcher = customMatchers.get(name);
		if (matcher != null) {
			return Optional.of(matcher);
		} else {
			return getNormalMatcher(name);
		}
	}
	
	protected abstract Optional<? extends Matcher<T>> getNormalMatcher(String name);

	public Matcher<T> getWildcardMatcher() {
		return wildcard;
	}
}
