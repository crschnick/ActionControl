package org.monospark.actioncontrol.kind.block;

import org.monospark.actioncontrol.kind.ObjectKind;
import org.spongepowered.api.block.BlockType;

public final class BlockKind extends ObjectKind {
	
	private static final BlockKindRegistry registry = new BlockKindRegistry();

	public static BlockKindRegistry getRegistry() {
		return registry;
	}
	
	private BlockType type;
	
	BlockKind(BlockType type, int variant) {
		super(variant);
		this.type = type;
	}

	public BlockType getBlockType() {
		return this.type;
	}

	@Override
	protected String getBaseName() {
		return this.type.getName();
	}
}
