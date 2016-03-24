package org.monospark.actioncontrol.rule.impl;

import org.monospark.actioncontrol.rule.ActionRule;
import org.monospark.actioncontrol.rule.filter.ActionFilterOption;
import org.monospark.actioncontrol.rule.filter.ActionFilterTemplate;
import org.monospark.spongematchers.type.MatcherType;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.event.block.ChangeBlockEvent;

public final class BlockPlaceRule extends ActionRule<ChangeBlockEvent.Place> {

    public BlockPlaceRule() {
        super("placeBlock", ChangeBlockEvent.Place.class);
    }

    @Override
    protected boolean acceptsEvent(ChangeBlockEvent.Place event) {
        return true;
    }

    @Override
    protected ActionFilterTemplate createFilter() {
        return ActionFilterTemplate.builder()
                .addOption(new ActionFilterOption<BlockSnapshot, ChangeBlockEvent.Place>("block",
                        MatcherType.BLOCK, e -> e.getTransactions().get(0).getFinal()))
                .build();
    }
}
