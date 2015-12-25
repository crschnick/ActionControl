package org.monospark.actioncontrol.matcher.object.block;

import org.monospark.actioncontrol.matcher.Matcher;
import org.monospark.actioncontrol.matcher.object.ObjectMatcherCreator;
import org.spongepowered.api.block.BlockSnapshot;

public final class BlockMatcherCreator extends ObjectMatcherCreator<BlockKind, BlockSnapshot> {

    public BlockMatcherCreator() {
        super(BlockKind.getRegistry());
    }

    @Override
    protected Matcher<BlockSnapshot> createWildcardMatcher() {
        return new Matcher<BlockSnapshot>() {

            @Override
            public boolean matches(BlockSnapshot o) {
                return true;
            }
        };
    }
}
