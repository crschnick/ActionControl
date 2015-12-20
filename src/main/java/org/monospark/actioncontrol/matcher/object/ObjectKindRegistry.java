package org.monospark.actioncontrol.matcher.object;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.monospark.actioncontrol.matcher.Matcher;
import org.monospark.actioncontrol.matcher.MatcherAmount;
import org.monospark.actioncontrol.matcher.MatcherCreator;

public abstract class ObjectKindRegistry<K extends ObjectKind & Matcher<T>, T> extends MatcherCreator<T> {

	private static final Pattern KIND_NAME_PATTERN = Pattern.compile("((\\w+:)?[a-zA-Z]\\w+)(:(\\d+)(-(\\d+))?)?");
	
	private static final int NAME = 1;
	private static final int MOD_PREFIX = 2;
	private static final int VARIANT_DATA = 3;
	private static final int DATA_VALUE = 4;
	private static final int DATA_RANGE_ADDITION = 5;
	private static final int DATA_RANGE_END = 6;

	private boolean init;

	protected abstract void init();

	public final Optional<? extends Matcher<T>> getNormalMatcher(String name) {
		Objects.requireNonNull(name, "Name must be not null");
		
		if(!this.init) {
			init();
			this.init = true;
		}
		
		Optional<String> formattedName = format(name);
		if(!formattedName.isPresent()) {
			return Optional.empty();
		}
		
		return createKindMatcher(formattedName.get());
	}
	
	private Optional<String> format(String name) {	
		java.util.regex.Matcher matcher = KIND_NAME_PATTERN.matcher(name);
		if(!matcher.matches()) {
			return Optional.empty();
		}
		String correctName = name;
		
		boolean hasModPrefix = matcher.group(MOD_PREFIX) != null;
		if(!hasModPrefix) {
			correctName = "minecraft:" + correctName;
		}
		
		boolean hasVariant = matcher.group(VARIANT_DATA) != null;
		if(!hasVariant) {
			correctName = correctName + ":0";
		}
		
		return Optional.of(correctName);
	}
	
	private Optional<? extends Matcher<T>> createKindMatcher(String name) {
		java.util.regex.Matcher matcher = KIND_NAME_PATTERN.matcher(name);
		matcher.matches();
		boolean isRange = matcher.group(DATA_RANGE_ADDITION) != null;
		if(isRange) {
			int start = Integer.valueOf(matcher.group(DATA_VALUE));
			int end = Integer.valueOf(matcher.group(DATA_RANGE_END));

			Set<Matcher<T>> matchers = new HashSet<Matcher<T>>();
			for (int i = start; i < end; i++) {
				Optional<K> kind = getKind(matcher.group(NAME), i);
				if(!kind.isPresent()) {
					return Optional.empty();
				}
				
				matchers.add(kind.get());
			}
			
			return Optional.of(new MatcherAmount<>(matchers));
		} else {
			return getKind(matcher.group(NAME), Integer.parseInt(matcher.group(DATA_VALUE)));
		}
	}
	
	protected abstract Optional<K> getKind(String baseName, int variant);
}
