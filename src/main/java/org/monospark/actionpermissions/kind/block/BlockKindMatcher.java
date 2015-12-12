package org.monospark.actionpermissions.kind.block;

import java.util.Optional;
import java.util.Set;

import org.monospark.actionpermissions.kind.ObjectMatcher;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.item.inventory.ItemStack;

public final class BlockKindMatcher implements ObjectMatcher {

	private Set<BlockKind> kinds;

	public BlockKindMatcher(Set<BlockKind> kinds) {
		this.kinds = kinds;
	}

	public boolean matchesBlockState(BlockState state) {
		for(BlockKind kind : kinds) {
			if(kind.getBlockType() == state.getType() &&
					(state.toContainer().getInt(new DataQuery("UnsafeMeta")).get() & kind.getVariant()) ==
							kind.getVariant()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean matchesItemStack(ItemStack stack) {
		Optional<BlockType> stackType = stack.getItem().getBlock();
		if(!stackType.isPresent()) {
			return false;
		}
		
		for(BlockKind kind : kinds) {
			if(kind.getBlockType() == stackType.get() &&
					(stack.toContainer().getInt(new DataQuery("UnsafeDamage")).get() & kind.getVariant()) ==
							kind.getVariant()) {
				return true;
			}
		}
		return false;
	}
}
