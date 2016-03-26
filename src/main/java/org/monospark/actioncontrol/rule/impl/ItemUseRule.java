package org.monospark.actioncontrol.rule.impl;

import org.monospark.actioncontrol.rule.ActionRule;
import org.monospark.actioncontrol.rule.filter.ActionFilterOption;
import org.monospark.actioncontrol.rule.filter.ActionFilterTemplate;
import org.monospark.spongematchers.type.MatcherType;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.inventory.ItemStack;

public final class ItemUseRule extends ActionRule<UseItemStackEvent.Start> {

    public ItemUseRule() {
        super("use-item", UseItemStackEvent.Start.class);
    }

    @Override
    protected boolean acceptsEvent(UseItemStackEvent.Start event) {
        return true;
    }

    @Override
    protected ActionFilterTemplate createFilter() {
        return ActionFilterTemplate.builder()
                .addOption(new ActionFilterOption<ItemStack, UseItemStackEvent.Start>("item",
                        MatcherType.ITEM_STACK, e -> e.getItemStackInUse().getOriginal().createStack()))
                .build();
    }
}
