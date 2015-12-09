package org.monospark.actionpermissions.kind.block;

import org.monospark.actionpermissions.kind.ObjectKind;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;

public class BlockKind implements ObjectKind {
	
	private static final BlockKindRegistry registry = new BlockKindRegistry();

	public static BlockKindRegistry getRegistry() {
		return registry;
	}

	private BlockType type;

	BlockKind(BlockType type) {
		this.type = type;
	}
	
	public boolean matchesState(BlockState state) {
		return state.getType() == getBlockType();
	}

	@Override
	public String getName() {
		return type.getName();
	}
	
	public BlockType getBlockType() {
		return this.type;
	}
}
