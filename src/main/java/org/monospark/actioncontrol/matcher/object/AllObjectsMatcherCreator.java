package org.monospark.actioncontrol.matcher.object;

import java.util.Optional;

import org.monospark.actioncontrol.matcher.Matcher;
import org.monospark.actioncontrol.matcher.MatcherCreator;
import org.monospark.actioncontrol.matcher.MatcherType;
import org.monospark.actioncontrol.matcher.object.block.BlockKind;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public final class AllObjectsMatcherCreator extends MatcherCreator<ItemStackSnapshot> {

    @Override
    protected Matcher<ItemStackSnapshot> createWildcardMatcher() {
        return new Matcher<ItemStackSnapshot>() {

            @Override
            public boolean matches(ItemStackSnapshot o) {
                return true;
            }
        };
    }

    @Override
    public Optional<? extends Matcher<ItemStackSnapshot>> getNormalMatcher(String name) {
        Optional<? extends Matcher<BlockSnapshot>> blockMatcher = MatcherType.BLOCK.getMatcher(name);
        if (blockMatcher.isPresent()) {
            BlockKind kind = (BlockKind) blockMatcher.get();
            return Optional.of(new Matcher<ItemStackSnapshot>() {

                @Override
                public boolean matches(ItemStackSnapshot stack) {
                    Optional<BlockType> type = stack.getType().getBlock();
                    if (!type.isPresent()) {
                        return false;
                    }

                    int damage = stack.toContainer().getInt(DataQuery.of("UnsafeDamage")).get();
                    return type.get() == kind.getBlockType() && (damage & kind.getVariant()) == kind.getVariant();
                }
            });
        }

        Optional<? extends Matcher<ItemStackSnapshot>> itemMatcher = MatcherType.ITEM.getMatcher(name);
        return itemMatcher;
    }
}
