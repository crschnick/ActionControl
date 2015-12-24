package org.monospark.actioncontrol.rules.impl;

import java.util.Optional;

import org.monospark.actioncontrol.matcher.MatcherType;
import org.monospark.actioncontrol.rules.ActionRuleSimple;
import org.monospark.actioncontrol.rules.filter.ActionFilterOption;
import org.monospark.actioncontrol.rules.filter.ActionFilterTemplate;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public final class BlockBreakRule extends ActionRuleSimple<ChangeBlockEvent.Break> {

    public BlockBreakRule() {
        super("breakBlock", ChangeBlockEvent.Break.class);
    }

    @Override
    protected ActionFilterTemplate createFilter() {
        return ActionFilterTemplate.builder()
                .addOption(new ActionFilterOption<BlockSnapshot, ChangeBlockEvent.Break>("blockIds",
                        MatcherType.BLOCK, e -> e.getTransactions().get(0).getOriginal()))
                .addOption(new ActionFilterOption<ItemStackSnapshot, ChangeBlockEvent.Break>("toolIds",
                        MatcherType.ITEM, (e) -> {
                                Optional<ItemStack> inHand = e.getCause().first(Player.class).get().getItemInHand();
                                return inHand.isPresent() ? inHand.get().createSnapshot() : null;
                        })).build();
    }
}
