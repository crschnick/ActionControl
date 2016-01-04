package org.monospark.actioncontrol.matcher.object.item;

import org.monospark.actioncontrol.matcher.Matcher;
import org.monospark.actioncontrol.matcher.object.ObjectKind;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public final class ItemKind extends ObjectKind implements Matcher<ItemStackSnapshot> {

    private static final ItemKindRegistry REGISTRY = new ItemKindRegistry();

    public static ItemKindRegistry getRegistry() {
        return REGISTRY;
    }

    private ItemType type;

    ItemKind(ItemType type, int variant) {
        super(type.getName(), variant);
        this.type = type;
    }

    @Override
    public boolean matches(ItemStackSnapshot stack) {
        if (stack == null) {
            return false;
        }

        int damage = stack.createStack().toContainer().getInt(DataQuery.of("UnsafeDamage")).get();
        return stack.getType() == type && (damage & getVariant()) == getVariant();
    }

    public ItemType getItemType() {
        return type;
    }
}
