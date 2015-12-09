package org.monospark.actionpermissions.kind.item;

import org.monospark.actionpermissions.kind.ObjectKind;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

public abstract class ItemKind implements ObjectKind {

	private static final ItemKindRegistry registry = new ItemKindRegistry();

	public static ItemKindRegistry getRegistry() {
		return registry;
	}
	
	private ItemType type;
	
	ItemKind(ItemType type) {
		this.type = type;
	}
	
	public abstract boolean matchesItemStack(ItemStack stack);

	public ItemType getItemType() {
		return type;
	}
}
