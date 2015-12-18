package org.monospark.actioncontrol.kind.object;

import java.util.Map;
import java.util.Optional;

import org.monospark.actioncontrol.kind.KindRegistry;
import org.monospark.actioncontrol.kind.KindType;
import org.monospark.actioncontrol.kind.matcher.KindMatcher;
import org.monospark.actioncontrol.kind.matcher.KindWildcardMatcher;

public final class ObjectKindRegistry extends KindRegistry {

	@Override
	protected void addCustomMatchers(Map<String, KindMatcher> matchers) {
		matchers.put("block:*", new KindWildcardMatcher(true, false));
		matchers.put("item:*", new KindWildcardMatcher(false, true));
		matchers.put("*", new KindWildcardMatcher(true, true));
	}
	
	@Override
	public Optional<? extends KindMatcher> getNormalMatcher(String name) {
		Optional<? extends KindMatcher> blockMatcher = KindType.BLOCK.getRegistry().getMatcher(name);
		if(blockMatcher.isPresent()) {
			return blockMatcher;
		}
		
		Optional<? extends KindMatcher> itemMatcher = KindType.ITEM.getRegistry().getMatcher(name);
		return itemMatcher;
	}
}
