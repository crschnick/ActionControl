package org.monospark.actionpermissions.kind.block;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;

public final class BlockKindRegistry {

	private static final Pattern BLOCK_KIND_PATTERN = Pattern.compile("(\\w+?:\\w+?)(?::(\\d+))?");
	
	private final Set<BlockKind> allKinds = new HashSet<BlockKind>();

	BlockKindRegistry() {}
	
	public void initRegistry() {
		for(BlockType type : Sponge.getRegistry().getAllOf(CatalogTypes.BLOCK_TYPE)) {
			Optional<BlockTrait<?>> variantTrait = type.getDefaultState().getTrait("variant");
			boolean hasVariants = variantTrait.isPresent();
			if (hasVariants) {
				Set<BlockKindVariant> variants = new HashSet<BlockKindVariant>();
				int variantNumber = 0;
				for (Object o : variantTrait.get().getPossibleValues()) {
					System.out.println(type);
					variants.add(new BlockKindVariant(type, variantNumber, o.toString()));
					variantNumber++;
				}
				allKinds.add(new BlockKindVariantAmount(type, variants));
			} else {
				BlockKind kind = new BlockKind(type);
				allKinds.add(kind);
			}
		}
	}
	
	public Optional<BlockKind> getBlockKind(String name) {
		Matcher matcher = BLOCK_KIND_PATTERN.matcher(name);
		if (!matcher.matches()) {
			return Optional.empty();
		}
		
		BlockKind matchedKind = null;
		for (BlockKind kind : allKinds) {
			if (kind.getBlockType().getName().equals(matcher.group(1))) {
				matchedKind = kind;
				break;
			}
		}
		if (matchedKind == null) {
			return Optional.empty();
		}
		
		if (matcher.groupCount() == 3) {
			int variantNumber = Integer.parseInt(matcher.group(2));
			if(!(matchedKind instanceof BlockKindVariantAmount)) {
				return Optional.empty();
			}
			
			for(BlockKindVariant variant : ((BlockKindVariantAmount) matchedKind).getVariants()) {
				if(variant.getVariantNumber() == variantNumber) {
					return Optional.of(variant);
				}
			}
			
			return Optional.empty();
		} else {
			return Optional.of(matchedKind);
		}
	}
	
//	public BlockKind getBlockKind(BlockState state) {
//		BlockKind kind = getByBlockType(state.getType());
//		Optional<BlockTrait<?>> variantTrait = state.getTrait("variant");
//		if(variantTrait.isPresent()) {
//			BlockKindVariantAmount variantAmount = (BlockKindVariantAmount) kind;
//			String variantName = variantTrait.get().getName();
//			BlockKindVariant variant = getVariantByName(variantAmount, variantName);
//			return variant;
//		} else {
//			return kind;
//		}
//	}
//	
//	private BlockKindVariant getVariantByName(BlockKindVariantAmount variantAmount, String name) {
//		for(BlockKindVariant variant : variantAmount.getVariants()) {
//			if(variant.getVariantName().equals(name)) {
//				return variant;
//			}
//		}
//		
//		throw new AssertionError();
//	}
//	
//	private BlockKind getByBlockType(BlockType type) {
//		for(BlockKind kind : allKinds) {
//			if(kind.getBlockType() == type) {
//				return kind;
//			}
//		}
//		
//		throw new AssertionError();
//	}
}
