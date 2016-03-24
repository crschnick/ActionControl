package org.monospark.actioncontrol.rule.impl;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.monospark.actioncontrol.rule.ActionRule;
import org.monospark.actioncontrol.rule.filter.ActionFilterOption;
import org.monospark.actioncontrol.rule.filter.ActionFilterTemplate;
import org.monospark.spongematchers.type.MatcherType;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent.NumberPress;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent.Primary;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent.Shift;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public final class CraftRule extends ActionRule<ClickInventoryEvent> {

    public CraftRule() {
        super("craft", ClickInventoryEvent.class);
    }

    @Override
    protected boolean acceptsEvent(ClickInventoryEvent event) {
        if (event instanceof Shift.Primary) {
            return getConsumedItemStacks(event.getTransactions()).size() > 1;
        } else if (event instanceof Primary) {
            if (event.getCursorTransaction().getFinal() == null) {
                return false;
            }
            return event.getTransactions().size() >= 2;
        } else if (event instanceof NumberPress) {
            //Ugly, but it's the only thing that works
            event.setCancelled(true);
        }
        return false;
    }

    @Override
    protected ActionFilterTemplate createFilter() {
        return ActionFilterTemplate.builder()
                .addOption(new ActionFilterOption<ItemStack, ClickInventoryEvent>("result",
                        MatcherType.ITEM_STACK, e -> {
                            if (e instanceof Shift.Primary) {
                                int quantity = getConsumedItemStacks(
                                        e.getTransactions()).iterator().next().getQuantity();
                                Entry<ItemStack, Integer> result = getCreatedItemStack(e.getTransactions());
                                int resultQuantity = result.getValue();
                                int craftedQuantity = resultQuantity / quantity;
                                ItemStack craftedStack = ItemStack.builder()
                                        .from(result.getKey())
                                        .quantity(craftedQuantity)
                                        .build();
                                return craftedStack;
                            } else if (e instanceof Primary) {
                                return e.getCursorTransaction().getFinal().createStack();
                            }
                            throw new AssertionError();
                        }))
                .build();
    }

    private Set<ItemStack> getConsumedItemStacks(List<SlotTransaction> transactions) {
        Set<ItemStack> consumed = Sets.newHashSet();
        for (SlotTransaction transaction : transactions) {
            if (transaction.getOriginal().getType() == ItemTypes.NONE) {
                continue;
            }

            int newCount = transaction.getFinal().getType() != ItemTypes.NONE ? transaction.getFinal().getCount() : 0;
            if (newCount >= transaction.getOriginal().getCount()) {
                continue;
            }

            int stackSize = transaction.getOriginal().getCount() - newCount;
            consumed.add(ItemStack.builder()
                    .from(transaction.getOriginal().createStack())
                    .quantity(stackSize)
                    .build());
        }
        return consumed;
    }

    private Entry<ItemStack, Integer> getCreatedItemStack(List<SlotTransaction> transactions) {
        ItemStack result = null;
        int created = 0;
        for (SlotTransaction transaction : transactions) {
            if (transaction.getFinal().getType() == ItemTypes.NONE) {
                continue;
            }

            int oldCount = transaction.getOriginal().getType() != ItemTypes.NONE
                    ? transaction.getOriginal().getCount() : 0;
            int newCount = transaction.getFinal().getType() != ItemTypes.NONE ? transaction.getFinal().getCount() : 0;
            if (newCount <= oldCount) {
                continue;
            }

            if (result == null) {
                result = ItemStack.builder()
                        .from(transaction.getFinal().createStack())
                        .quantity(1)
                        .build();
            }
            created += newCount - oldCount;
        }
        return Maps.immutableEntry(result, created);
    }
}
