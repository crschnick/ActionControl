package org.monospark.actioncontrol.rule.impl;

import org.monospark.actioncontrol.matcher.MatcherType;
import org.monospark.actioncontrol.rule.ActionRule;
import org.monospark.actioncontrol.rule.filter.ActionFilterOption;
import org.monospark.actioncontrol.rule.filter.ActionFilterTemplate;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public final class UseItemRule extends ActionRule<UseItemStackEvent.Start> {

    public UseItemRule() {
        super("useItem", UseItemStackEvent.Start.class);
    }

    @Override
    protected ActionFilterTemplate createFilter() {
        return ActionFilterTemplate.builder()
                .addOption(new ActionFilterOption<ItemStackSnapshot, UseItemStackEvent.Start>("itemIds",
                        MatcherType.ITEM, e -> e.getItemStackInUse().getOriginal()))
                .build();
    }
}
