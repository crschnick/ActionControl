package org.monospark.actioncontrol.rules.impl;

import org.monospark.actioncontrol.matcher.MatcherType;
import org.monospark.actioncontrol.rules.ActionRuleSimple;
import org.monospark.actioncontrol.rules.filter.ActionFilterOption;
import org.monospark.actioncontrol.rules.filter.ActionFilterTemplate;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public final class UseItemRule extends ActionRuleSimple<UseItemStackEvent.Start> {

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
