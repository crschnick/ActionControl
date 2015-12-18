package org.monospark.actioncontrol.kind.object;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.monospark.actioncontrol.kind.Kind;
import org.monospark.actioncontrol.kind.KindRegistry;
import org.monospark.actioncontrol.kind.matcher.KindMatcher;
import org.monospark.actioncontrol.kind.matcher.KindMatcherAmount;

public abstract class ObjectKindRegistryBase<K extends Kind> extends KindRegistry {

	private static final Pattern KIND_NAME_PATTERN = Pattern.compile("((\\w+:)?[a-zA-Z]\\w+)(:(\\d+)(-(\\d+))?)?");
	
	private static final int NAME = 1;
	private static final int MOD_PREFIX = 2;
	private static final int VARIANT_DATA = 3;
	private static final int DATA_VALUE = 4;
	private static final int DATA_RANGE_ADDITION = 5;
	private static final int DATA_RANGE_END = 6;

	private boolean init;

	protected abstract void init();
	
	protected abstract void addCustomMatchers(Map<String,KindMatcher> matchers);
	
	public final Optional<? extends KindMatcher> getNormalMatcher(String name) {
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
		Matcher matcher = KIND_NAME_PATTERN.matcher(name);
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
	
	private Optional<? extends KindMatcher> createKindMatcher(String name) {
		Matcher matcher = KIND_NAME_PATTERN.matcher(name);
		matcher.matches();
		boolean isRange = matcher.group(DATA_RANGE_ADDITION) != null;
		if(isRange) {
			int start = Integer.valueOf(matcher.group(DATA_VALUE));
			int end = Integer.valueOf(matcher.group(DATA_RANGE_END));

			KindMatcherAmount.Builder builder = KindMatcherAmount.builder();
			for (int i = start; i < end; i++) {
				Optional<K> kind = getKind(matcher.group(NAME), i);
				if(!kind.isPresent()) {
					return Optional.empty();
				}
				
				builder.addKindMatcher(kind.get());
			}
			
			return Optional.of(builder.build());
		} else {
			return getKind(matcher.group(NAME), Integer.parseInt(matcher.group(DATA_VALUE)));
		}
	}
	
	protected abstract Optional<K> getKind(String baseName, int variant);
}
