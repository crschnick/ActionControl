package org.monospark.actionpermissions.kind;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.monospark.actionpermissions.kind.block.BlockKind;
import org.monospark.actionpermissions.kind.block.BlockKindMatcher;
import org.monospark.actionpermissions.kind.item.ItemKind;
import org.monospark.actionpermissions.kind.item.ItemKindMatcher;

public abstract class ObjectKindRegistry<K extends ObjectKind, M extends ObjectMatcher> {

	private static final Pattern KIND_NAME_PATTERN = Pattern.compile("((\\w+:)?(\\w+))(:(\\d+)(-\\d+)?)?");
	
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
		
		String formattedName = format(name);
		Optional<Set<K>> kinds = getContainedKinds(formattedName);
		if(!kinds.isPresent()) {
			return Optional.empty();
		}

		return Optional.of(createMatcher(kinds.get()));
	}
	
	private String format(String name) {
		Matcher matcher = KIND_NAME_PATTERN.matcher(name);
		String correctName = name;
		
		boolean hasModPrefix = matcher.group(2) != null;
		if(!hasModPrefix) {
			correctName = "minecraft:" + correctName;
		}
		
		boolean hasVariant = matcher.group(4) != null;
		if(!hasVariant) {
			correctName = correctName + ":0";
		}
		
		return correctName;
	}
	
	private Optional<Set<K>> getContainedKinds(String name) {
		Matcher matcher = KIND_NAME_PATTERN.matcher(name);
		boolean isRange = matcher.group(5) != null;
		if(isRange) {
			String[] split = matcher.group(4).split("-");
			int start = Integer.valueOf(split[0]);
			int end = Integer.valueOf(split[1]);
			
			Set<K> variants = new HashSet<K>();
			for (int i = start; i < end; i++) {
				Optional<K> kind = getKind(matcher.group(1), i);
				if(!kind.isPresent()) {
					return Optional.empty();
				}
				
				variants.add(kind.get());
			}
			return Optional.of(variants);
		} else {
			Optional<K> kind = getKind(matcher.group(1), Integer.parseInt(matcher.group(4)));
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
