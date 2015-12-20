package org.monospark.actioncontrol.matcher.object.all;

import java.util.Optional;

import org.monospark.actioncontrol.matcher.Matcher;
import org.monospark.actioncontrol.matcher.MatcherCreator;
import org.monospark.actioncontrol.matcher.MatcherType;
import org.monospark.actioncontrol.matcher.object.block.BlockKind;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.item.inventory.ItemStack;

public final class AllKindRegistry extends MatcherCreator<ItemStack> {

	@Override
	protected Matcher<ItemStack> createWildcardMatcher() {
		return new Matcher<ItemStack>() {

			@Override
			public boolean matches(ItemStack o) {
				return true;
			}
		};
	}
	
	@Override
	public Optional<? extends Matcher<ItemStack>> getNormalMatcher(String name) {
		Optional<? extends Matcher<BlockState>> blockMatcher = MatcherType.BLOCK.getMatcher(name);
		if(blockMatcher.isPresent()) {
			BlockKind kind = (BlockKind) blockMatcher.get();
			return Optional.of(new Matcher<ItemStack>() {

				@Override
				public boolean matches(ItemStack stack) {
					Optional<BlockType> type = stack.getItem().getBlock();
					if(!type.isPresent()) {
						return false;
					}
					
					int damage = stack.toContainer().getInt(new DataQuery("UnsafeDamage")).get();
					return type.get() == kind.getBlockType() && (damage & kind.getVariant()) == kind.getVariant();
				}
			});
		}
		
		Optional<? extends Matcher<ItemStack>> itemMatcher = MatcherType.ITEM.getMatcher(name);
		return itemMatcher;
	}
}
