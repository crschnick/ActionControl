package org.monospark.actioncontrol.rule.impl;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.monospark.actioncontrol.rule.ActionRule;
import org.monospark.actioncontrol.rule.filter.ActionFilterOption;
import org.monospark.actioncontrol.rule.filter.ActionFilterTemplate;
import org.monospark.spongematchers.type.MatcherType;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent.NumberPress;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent.Primary;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent.Secondary;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent.Shift;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackComparators;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public final class CraftRule extends ActionRule<ClickInventoryEvent> {

    public CraftRule() {
        super("craft", ClickInventoryEvent.class);
    }

    @Override
    protected boolean acceptsEvent(ClickInventoryEvent event) {
        if (event instanceof Shift.Primary || event instanceof Shift.Secondary) {
            return getConsumedItemStacks(event.getTransactions()).size() > 1;
        } else if (event instanceof Primary || event instanceof Secondary) {
            if (event.getCursorTransaction().getFinal() == null) {
                return false;
            }

            if (event.getTransactions().size() >= 2) {
                //It is not possible to craft anymore items because
                //at least one crafting ingredient got completely consumed.
                return true;
            } else if (event.getTransactions().size() >= 1) {
                Set<ItemStack> consumedStacks = getConsumedItemStacks(event.getTransactions());

                //An item stack was placed from the cursor into the inventory
                if (consumedStacks.size() == 0) {
                    return false;
                }

                ItemStack consumed = consumedStacks.iterator().next();
                ItemStack created = event.getCursorTransaction().getFinal().createStack();
                return !(ItemStackComparators.ALL.compare(consumed, created) == 0);
            }
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
                            if (e instanceof Shift.Primary || e instanceof Shift.Secondary) {
                                Set<ItemStack> consumed = getConsumedItemStacks(e.getTransactions());
                                Entry<ItemStack, Integer> result = getResult(e.getTransactions());
                                int ingredientsQuantity = getIngredientsQuantity(consumed, result.getKey());
                                int resultQuantity = result.getValue();
                                int craftedQuantity = resultQuantity / ingredientsQuantity;
                                ItemStack craftedStack = createStackWithCustomQuantity(result.getKey(),
                                        craftedQuantity);
                                return craftedStack;
                            } else if (e instanceof Primary || e instanceof Secondary) {
                                return e.getCursorTransaction().getFinal().createStack();
                            }
                            throw new AssertionError();
                        }))
                .build();
    }

    private int getIngredientsQuantity(Set<ItemStack> consumed, ItemStack result) {
        Set<ItemStack> ingredients = consumed.stream()
                .filter(s -> {
                    ItemStack oneQuantity = createStackWithCustomQuantity(s, 1);
                    return ItemStackComparators.ALL.compare(oneQuantity, result) != 0;
                })
                .collect(Collectors.toSet());
        return ingredients.iterator().next().getQuantity();
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
            consumed.add(createStackWithCustomQuantity(transaction.getOriginal().createStack(), stackSize));
        }
        return consumed;
    }

    private Entry<ItemStack, Integer> getResult(List<SlotTransaction> transactions) {
        ItemStack result = null;
        int created = 0;
        for (SlotTransaction transaction : transactions) {
            if (transaction.getFinal().getType() == ItemTypes.NONE) {
                continue;
            }

            int oldCount = transaction.getOriginal().getType() != ItemTypes.NONE
                    ? transaction.getOriginal().getCount() : 0;
            int newCount = transaction.getFinal().getCount();
            if (newCount <= oldCount) {
                continue;
            }

            if (result == null) {
                result = createStackWithCustomQuantity(transaction.getFinal().createStack(), 1);
            }
            created += newCount - oldCount;
        }
        return Maps.immutableEntry(result, created);
    }

    private ItemStack createStackWithCustomQuantity(ItemStack stack, int quantity) {
        return ItemStack.builder()
                .from(stack)
                .fromContainer(stack.toContainer())
                .quantity(quantity)
                .build();
    }
}
