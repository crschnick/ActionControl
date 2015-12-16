package org.monospark.actioncontrol.kind.object.item;

import org.monospark.actioncontrol.kind.Kind;
import org.monospark.actioncontrol.kind.KindType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

public final class ItemKind extends Kind {

	private ItemType type;
	
	ItemKind(ItemType type, int variant) {
		super(type.getName(), variant, KindType.ITEM);
		this.type = type;
	}
	
	@Override
	public boolean matchesBlockState(BlockState state) {
		return false;
	}

	@Override
	public boolean matchesItemStack(ItemStack stack) {
		int damage = stack.toContainer().getInt(new DataQuery("UnsafeDamage")).get();
		return stack.getItem().equals(type) && (damage & getVariant()) == getVariant();
	}
	
	public ItemType getItemType() {
		return type;
	}
}
