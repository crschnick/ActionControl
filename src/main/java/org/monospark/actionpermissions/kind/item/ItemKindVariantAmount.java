package org.monospark.actionpermissions.kind.item;

import java.util.Collections;
import java.util.Set;

import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

public final class ItemKindVariantAmount extends ItemKind {

	private Set<ItemKindVariant> variants;
	
	ItemKindVariantAmount(ItemType type, Set<ItemKindVariant> variants) {
		super(type);
		this.variants = variants;
	}
	
	@Override
	public boolean matchesItemStack(ItemStack stack) {
		return stack.getItem() == getItemType();
	}	

	@Override
	public String getName() {
		return getItemType().getName();
	}
	
	void addVariant(ItemKindVariant variant) {
		this.variants.add(variant);
	}
	
	public Set<ItemKindVariant> getVariants() {
		return Collections.unmodifiableSet(variants);
	}
}
