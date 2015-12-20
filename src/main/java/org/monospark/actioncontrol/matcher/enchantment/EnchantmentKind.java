package org.monospark.actioncontrol.matcher.enchantment;

import org.monospark.actioncontrol.matcher.Matcher;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.item.Enchantment;

public final class EnchantmentKind implements Matcher<ItemEnchantment> {

	private Enchantment enchantment;
	
	private int level;
	
	EnchantmentKind(Enchantment enchantment, int level) {
		this.enchantment = enchantment;
		this.level = level;
	}
	
	@Override
	public boolean matches(ItemEnchantment o) {
		return o.getEnchantment() == enchantment && o.getLevel() == level;
	}

	public Enchantment getEnchantment() {
		return enchantment;
	}

	public int getLevel() {
		return level;
	}
}
