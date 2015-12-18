package org.monospark.actioncontrol.kind;

import java.util.Map;
import java.util.Optional;

import org.monospark.actioncontrol.kind.matcher.KindMatcher;

import com.google.common.collect.Maps;

public abstract class KindRegistry {

	private Map<String,KindMatcher> customMatchers;

	protected KindRegistry() {
		customMatchers = Maps.newHashMap();
		addCustomMatchers(customMatchers);
	}
	
	protected abstract void addCustomMatchers(Map<String,KindMatcher> matchers);
	
	public final Optional<? extends KindMatcher> getMatcher(String name) {
		KindMatcher matcher = customMatchers.get(name);
		if (matcher != null) {
			return Optional.of(matcher);
		} else {
			return getNormalMatcher(name);
		}
	}
	
	protected abstract Optional<? extends KindMatcher> getNormalMatcher(String name);
}
