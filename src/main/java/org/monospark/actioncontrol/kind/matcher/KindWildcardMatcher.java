package org.monospark.actioncontrol.kind.matcher;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.item.inventory.ItemStack;

public final class KindWildcardMatcher implements KindMatcher {

	private boolean blocks;
	
	private boolean items;

	public KindWildcardMatcher(boolean blocks, boolean items) {
		this.blocks = blocks;
		this.items = items;
	}

	@Override
	public boolean matchesBlockState(BlockState state) {
		return blocks;
	}

	@Override
	public boolean matchesItemStack(ItemStack stack) {
		return items;
	}
}
