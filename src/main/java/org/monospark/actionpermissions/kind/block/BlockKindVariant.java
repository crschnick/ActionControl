package org.monospark.actionpermissions.kind.block;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;

public final class BlockKindVariant extends BlockKind {

	private int variant;
	
	private String variantName;
	
	BlockKindVariant(BlockType type, int variant, String variantName) {
		super(type);
		this.variant = variant;
		this.variantName = variantName;
	}
	
	@Override
	public boolean matchesState(BlockState state) {
		if (!super.matchesState(state)) {
			return false;
		}
		
		String variantName = state.getTrait("variant").get().getName();
		return this.variantName.equals(variantName);
	}

	@Override
	public String getName() {
		return this.getBlockType().getName() + ":" + variant;
	}
	
	public int getVariantNumber() {
		return variant;
	}

	public String getVariantName() {
		return variantName;
	}
}
