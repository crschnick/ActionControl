package org.monospark.actionpermissions.kind.item;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

public final class ItemKindVariant extends ItemKind {

	private int variant;

	ItemKindVariant(ItemType type, int variant) {
		super(type);
		this.variant = variant;
	}

	@Override
	public boolean matchesItemStack(ItemStack stack) {
		int damageValue = stack.toContainer().getInt(new DataQuery("UnsafeDamage")).get();
		return this.getItemType() == stack.getItem() && this.variant == damageValue;
	}

	@Override
	public String getName() {
		return this.getItemType().getName() + ":" + variant;
	}
	
	public int getVariantNumber() {
		return variant;
	}
}
