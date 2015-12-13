package org.monospark.actioncontrol.kind;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.monospark.actioncontrol.kind.block.BlockKind;
import org.monospark.actioncontrol.kind.block.BlockKindMatcher;
import org.monospark.actioncontrol.kind.item.ItemKind;
import org.monospark.actioncontrol.kind.item.ItemKindMatcher;

public abstract class ObjectKindRegistry<K extends ObjectKind, M extends ObjectMatcher> {

	private static final Pattern KIND_NAME_PATTERN = Pattern.compile("((\\w+:)?[a-zA-Z]\\w+)(:(\\d+)(-(\\d+))?)?");
	
	private static final int NAME = 1;
	private static final int MOD_PREFIX = 2;
	private static final int VARIANT_DATA = 3;
	private static final int DATA_VALUE = 4;
	private static final int DATA_RANGE_ADDITION = 5;
	private static final int DATA_RANGE_END = 6;
	
	public static Optional< ? extends ObjectMatcher> getObjectKindMatcher(String name) {
		Optional<BlockKindMatcher> blockMatcher = BlockKind.getRegistry().get(name);
		if(blockMatcher.isPresent()) {
			return blockMatcher;
		}
		
		Optional<ItemKindMatcher> itemMatcher = ItemKind.getRegistry().get(name);
		return itemMatcher;
	}
	
	private boolean init;

	protected ObjectKindRegistry() {}
	
	protected abstract void init();
	
	public final Optional<M> get(String name) {
		Objects.requireNonNull(name, "Name must be not null");
		
		if(!this.init) {
			init();
			this.init = true;
		}
		
		Optional<String> formattedName = format(name);
		if(!formattedName.isPresent()) {
			return Optional.empty();
		}
		
		Optional<Set<K>> kinds = getContainedKinds(formattedName.get());
		if(!kinds.isPresent()) {
			return Optional.empty();
		}

		return Optional.of(createMatcher(kinds.get()));
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
	
	private Optional<Set<K>> getContainedKinds(String name) {
		Matcher matcher = KIND_NAME_PATTERN.matcher(name);
		matcher.matches();
		boolean isRange = matcher.group(DATA_RANGE_ADDITION) != null;
		if(isRange) {
			int start = Integer.valueOf(matcher.group(DATA_VALUE));
			int end = Integer.valueOf(matcher.group(DATA_RANGE_END));
			
			Set<K> variants = new HashSet<K>();
			for (int i = start; i < end; i++) {
				Optional<K> kind = getKind(matcher.group(NAME), i);
				if(!kind.isPresent()) {
					return Optional.empty();
				}
				
				variants.add(kind.get());
			}
			return Optional.of(variants);
		} else {
			Optional<K> kind = getKind(matcher.group(NAME), Integer.parseInt(matcher.group(DATA_VALUE)));
			if(!kind.isPresent()) {
				return Optional.empty();
			}
			return Optional.of(Collections.singleton(kind.get()));
		}
	}
	
	protected abstract Optional<K> getKind(String baseName, int variant);
	
	protected abstract M createMatcher(Set<K> kinds);

	public boolean isInitialized() {
		return init;
	}
}
