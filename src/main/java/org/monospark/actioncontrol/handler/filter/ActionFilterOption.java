package org.monospark.actioncontrol.handler.filter;

import java.util.function.Function;

import org.monospark.actioncontrol.matcher.MatcherType;
import org.spongepowered.api.event.Event;

public final class ActionFilterOption<T, E extends Event> {

	private String name;
	
	private MatcherType<T> type;

	private Function<E, T> function;

	public ActionFilterOption(String name, MatcherType<T> type, Function<E, T> function) {
		this.name = name;
		this.type = type;
		this.function = function;
	}

	public String getName() {
		return name;
	}

	public MatcherType<T> getType() {
		return type;
	}

	public Function<E, T> getFunction() {
		return function;
	}
}
