package org.monospark.actioncontrol.handler.impl;

import java.util.Optional;

import org.monospark.actioncontrol.handler.ActionHandler;
import org.monospark.actioncontrol.handler.filter.ActionFilterOption;
import org.monospark.actioncontrol.handler.filter.ActionFilterTemplate;
import org.monospark.actioncontrol.matcher.MatcherType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.item.inventory.ItemStack;

public final class BlockBreakHandler extends ActionHandler<ChangeBlockEvent.Break> {

	public BlockBreakHandler() {
		super("breakBlock", ChangeBlockEvent.Break.class);
	}

	@Override
	protected ActionFilterTemplate createFilter() {
		return ActionFilterTemplate.builder()
				.addOption(new ActionFilterOption<BlockState, ChangeBlockEvent.Break>("blockIds",
						MatcherType.BLOCK, e -> e.getTransactions().get(0).getOriginal().getState()))
				.addOption(new ActionFilterOption<ItemStack, ChangeBlockEvent.Break>("toolIds",
						MatcherType.ITEM, (e) -> {
							Optional<ItemStack> inHand = e.getCause().first(Player.class).get().getItemInHand();
							return inHand.isPresent() ? inHand.get() : null;
						}))
				.build();
	}
}
