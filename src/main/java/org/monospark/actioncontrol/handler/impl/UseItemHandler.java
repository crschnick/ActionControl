package org.monospark.actioncontrol.handler.impl;

import org.monospark.actioncontrol.handler.ActionHandlerSimple;
import org.monospark.actioncontrol.handler.filter.ActionFilterOption;
import org.monospark.actioncontrol.handler.filter.ActionFilterTemplate;
import org.monospark.actioncontrol.matcher.MatcherType;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public final class UseItemHandler extends ActionHandlerSimple<UseItemStackEvent.Start> {

    public UseItemHandler() {
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
