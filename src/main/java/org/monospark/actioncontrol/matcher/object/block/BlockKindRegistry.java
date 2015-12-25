package org.monospark.actioncontrol.matcher.object.block;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.monospark.actioncontrol.matcher.object.ObjectKindRegistry;
import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;

public final class BlockKindRegistry extends ObjectKindRegistry<BlockKind> {

    private Set<BlockKind> allKinds;

    BlockKindRegistry() {
        this.allKinds = new HashSet<BlockKind>();
    }

    protected void init() {
        for (BlockType type : Sponge.getRegistry().getAllOf(CatalogTypes.BLOCK_TYPE)) {
            Optional<BlockTrait<?>> variantTrait = type.getDefaultState().getTrait("variant");
            boolean hasVariants = variantTrait.isPresent();
            if (hasVariants) {
                for (int i = 0; i < variantTrait.get().getPossibleValues().size(); i++) {
                    allKinds.add(new BlockKind(type, i));
                }
            } else {
                BlockKind kind = new BlockKind(type, 0);
                allKinds.add(kind);
            }
        }
    }

    @Override
    protected Optional<BlockKind> getKind(String baseName, int variant) {
        for (BlockKind kind : allKinds) {
            if (kind.getBaseName().equals(baseName) && kind.getVariant() == variant) {
                return Optional.of(kind);
            }
        }
        return Optional.empty();
    }
}
