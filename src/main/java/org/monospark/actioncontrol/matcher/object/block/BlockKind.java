package org.monospark.actioncontrol.matcher.object.block;

import org.monospark.actioncontrol.matcher.Matcher;
import org.monospark.actioncontrol.matcher.object.ObjectKind;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataQuery;

public final class BlockKind extends ObjectKind implements Matcher<BlockSnapshot> {

	private BlockType type;
	
	BlockKind(BlockType type, int variant) {
		super(type.getName(), variant);
		this.type = type;
	}
	
	@Override
	public boolean matches(BlockSnapshot block) {
		int meta = block.toContainer().getInt(new DataQuery("UnsafeMeta")).get();
		return type.equals(block.getState().getType()) && (meta & getVariant()) == getVariant();
	}
	
	
	public BlockType getBlockType() {
		return this.type;
	}
}
