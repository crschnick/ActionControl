package org.monospark.actioncontrol.kind.object;

import java.util.Optional;

import org.monospark.actioncontrol.kind.KindRegistry;
import org.monospark.actioncontrol.kind.KindType;
import org.monospark.actioncontrol.kind.matcher.KindMatcher;
import org.monospark.actioncontrol.kind.matcher.KindWildcardMatcher;

public final class ObjectKindRegistry implements KindRegistry {

	private static final KindWildcardMatcher BLOCK_WILDCARD = new KindWildcardMatcher(true, false);
	private static final KindWildcardMatcher ITEM_WILDCARD = new KindWildcardMatcher(false, true);
	private static final KindWildcardMatcher OBJECT_WILDCARD = new KindWildcardMatcher(true, true);
	
	@Override
	public Optional<? extends KindMatcher> getMatcher(String name) {
		Optional<? extends KindMatcher> wildcardMatcher = getWildcardMatcher(name);
		if(wildcardMatcher.isPresent()) {
			return wildcardMatcher;
		}
		
		Optional<? extends KindMatcher> blockMatcher = KindType.BLOCK.getRegistry().getMatcher(name);
		if(blockMatcher.isPresent()) {
			return blockMatcher;
		}
		
		Optional<? extends KindMatcher> itemMatcher = KindType.ITEM.getRegistry().getMatcher(name);
		return itemMatcher;
	}
	
	private Optional<? extends KindMatcher> getWildcardMatcher(String name) {	
		if(name.equals("block:*")) {
			return Optional.of(BLOCK_WILDCARD);
		} else if(name.equals("item:*")) {
			return Optional.of(ITEM_WILDCARD);
		} else if(name.equals("*")) {
			return Optional.of(OBJECT_WILDCARD);
		} else {
			return Optional.empty();
		}
	}
}
