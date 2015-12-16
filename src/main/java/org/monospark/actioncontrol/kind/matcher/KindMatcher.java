package org.monospark.actioncontrol.kind.matcher;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.item.inventory.ItemStack;

public interface KindMatcher {

	boolean matchesBlockState(BlockState state);
	
	boolean matchesItemStack(ItemStack stack);
}
