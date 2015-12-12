package org.monospark.actionpermissions.kind;

import org.spongepowered.api.item.inventory.ItemStack;

public interface ObjectMatcher {

	boolean matchesItemStack(ItemStack stack);
}
