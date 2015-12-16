package org.monospark.actioncontrol.kind.matcher;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.item.inventory.ItemStack;

public final class KindMatcherAmount implements KindMatcher {

	private Set<KindMatcher> matchers;

	private KindMatcherAmount(Set<KindMatcher> matchers) {
		this.matchers = Collections.unmodifiableSet(matchers);
	}

	@Override
	public boolean matchesBlockState(BlockState state) {
		for(KindMatcher matcher : matchers) {
			if(matcher.matchesBlockState(state)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean matchesItemStack(ItemStack stack) {
		for(KindMatcher matcher : matchers) {
			if(matcher.matchesItemStack(stack)) {
				return true;
			}
		}
		return false;
	}
	
	public Set<KindMatcher> getMatchers() {
		return matchers;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		
		private Set<KindMatcher> matchers;
		
		private Builder() {
			this.matchers = new HashSet<KindMatcher>();
		}
		
		public Builder addKindMatcher(KindMatcher matcher) {
			matchers.add(matcher);
			return this;
		}
		
		public KindMatcherAmount build() {
			return new KindMatcherAmount(this.matchers);
		}
	}
}
