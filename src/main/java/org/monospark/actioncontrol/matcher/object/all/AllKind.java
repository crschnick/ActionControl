package org.monospark.actioncontrol.matcher.object.all;

import org.monospark.actioncontrol.matcher.Matcher;
import org.monospark.actioncontrol.matcher.object.ObjectKind;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.item.inventory.ItemStack;

public class AllKind extends ObjectKind implements Matcher<ItemStack> {

	protected AllKind(String name, int variant) {
		super(name, variant);
	}

	@Override
	public boolean matches(ItemStack stack) {
		if(stack == null) {
			return false;
		}
		
		int damage = stack.toContainer().getInt(new DataQuery("UnsafeDamage")).get();
		return stack.getItem().getName().equals(getName()) && (damage & getVariant()) == getVariant();
	}

}
