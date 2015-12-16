package org.monospark.actioncontrol.kind.object.block;

import org.monospark.actioncontrol.kind.Kind;
import org.monospark.actioncontrol.kind.KindType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.item.inventory.ItemStack;

public final class BlockKind extends Kind {

	private BlockType type;
	
	BlockKind(BlockType type, int variant) {
		super(type.getName(), variant, KindType.BLOCK);
		this.type = type;
	}
	
	@Override
	public boolean matchesBlockState(BlockState state) {
		int meta = state.toContainer().getInt(new DataQuery("UnsafeMeta")).get();
		return type.equals(state.getType()) && (meta & getVariant()) == getVariant();
	}

	@Override
	public boolean matchesItemStack(ItemStack stack) {
		return false;
	}	
	
	public BlockType getBlockType() {
		return this.type;
	}
}
