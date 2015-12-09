package org.monospark.actionpermissions.kind.block;

import java.util.Collections;
import java.util.Set;

import org.spongepowered.api.block.BlockType;

public final class BlockKindVariantAmount extends BlockKind {

	private Set<BlockKindVariant> variants;
	
	BlockKindVariantAmount(BlockType type, Set<BlockKindVariant> variants) {
		super(type);
		this.variants = Collections.unmodifiableSet(variants);
	}

	public Set<BlockKindVariant> getVariants() {
		return variants;
	}	
}
