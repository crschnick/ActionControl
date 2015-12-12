package org.monospark.actionpermissions.kind.item;

import java.util.Set;

import org.monospark.actionpermissions.kind.ObjectMatcher;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

public final class ItemKindMatcher implements ObjectMatcher {

	private Set<ItemKind> kinds;

	public ItemKindMatcher(Set<ItemKind> kinds) {
		this.kinds = kinds;
	}

	@Override
	public boolean matchesItemStack(ItemStack stack) {
		ItemType stackType = stack.getItem();
		for(ItemKind kind : kinds) {
			if(kind.getItemType() == stackType &&
					(stack.toContainer().getInt(new DataQuery("UnsafeDamage")).get() & kind.getVariant()) ==
							kind.getVariant()) {
				return true;
			}
		}
		return false;
	}
}
