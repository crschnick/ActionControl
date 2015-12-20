package org.monospark.actioncontrol.matcher;

import java.util.Optional;

import org.monospark.actioncontrol.matcher.enchantment.EnchantmentKindRegistry;
import org.monospark.actioncontrol.matcher.entity.EntityKindRegistry;
import org.monospark.actioncontrol.matcher.object.all.AllKindRegistry;
import org.monospark.actioncontrol.matcher.object.block.BlockKindRegistry;
import org.monospark.actioncontrol.matcher.object.item.ItemKindRegistry;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.item.inventory.ItemStack;

public final class MatcherType<T> {

	public static final MatcherType<BlockState> BLOCK = new MatcherType<>(new BlockKindRegistry());
	
	public static final MatcherType<ItemStack> ITEM = new MatcherType<>(new ItemKindRegistry());
	
	public static final MatcherType<ItemStack> OBJECT = new MatcherType<>(new AllKindRegistry());
	
	public static final MatcherType<Entity> ENTITY = new MatcherType<>(new EntityKindRegistry());
	
	public static final MatcherType<ItemEnchantment> ENCHANTMENT = new MatcherType<>(new EnchantmentKindRegistry());
	
	private MatcherCreator<T> creator;

	private MatcherType(MatcherCreator<T> creator) {
		this.creator = creator;
	}

	public Matcher<T> getDefaultMatcher() {
		return creator.getWildcardMatcher();
	}
	
	public Optional<? extends Matcher<T>> getMatcher(String name) {
		return creator.getMatcher(name);
	}
}
