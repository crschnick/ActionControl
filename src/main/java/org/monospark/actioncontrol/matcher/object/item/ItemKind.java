package org.monospark.actioncontrol.matcher.object.item;

import org.monospark.actioncontrol.matcher.Matcher;
import org.monospark.actioncontrol.matcher.object.ObjectKind;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

public final class ItemKind extends ObjectKind implements Matcher<ItemStack> {

	private ItemType type;
	
	ItemKind(ItemType type, int variant) {
		super(type.getName(), variant);
		this.type = type;
	}

	@Override
	public boolean matches(ItemStack stack) {
		if(stack == null) {
			return false;
		}
		
		int damage = stack.toContainer().getInt(new DataQuery("UnsafeDamage")).get();
		return stack.getItem().equals(type) && (damage & getVariant()) == getVariant();
	}
	
	public ItemType getItemType() {
		return type;
	}
}
