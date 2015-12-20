package org.monospark.actioncontrol.matcher.object.block;

import org.monospark.actioncontrol.matcher.Matcher;
import org.monospark.actioncontrol.matcher.object.ObjectKind;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataQuery;

public final class BlockKind extends ObjectKind implements Matcher<BlockState> {

	private BlockType type;
	
	BlockKind(BlockType type, int variant) {
		super(type.getName(), variant);
		this.type = type;
	}
	
	@Override
	public boolean matches(BlockState state) {
		int meta = state.toContainer().getInt(new DataQuery("UnsafeMeta")).get();
		return type.equals(state.getType()) && (meta & getVariant()) == getVariant();
	}
	
	
	public BlockType getBlockType() {
		return this.type;
	}
}
