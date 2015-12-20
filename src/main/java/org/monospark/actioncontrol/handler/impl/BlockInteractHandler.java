package org.monospark.actioncontrol.handler.impl;

import java.util.Optional;

import org.monospark.actioncontrol.handler.ActionHandler;
import org.monospark.actioncontrol.handler.filter.ActionFilterOption;
import org.monospark.actioncontrol.handler.filter.ActionFilterTemplate;
import org.monospark.actioncontrol.matcher.MatcherType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.item.inventory.ItemStack;

public final class BlockInteractHandler extends ActionHandler<InteractBlockEvent>{

	public BlockInteractHandler() {
		super("interactWithBlock", InteractBlockEvent.class);
	}

	@Override
	protected ActionFilterTemplate createFilter() {
		return ActionFilterTemplate.builder()
				.addOption(new ActionFilterOption<BlockState, InteractBlockEvent>("blockIds",
						MatcherType.BLOCK, e -> e.getTargetBlock().getState()))
				.addOption(new ActionFilterOption<ItemStack, InteractBlockEvent>("itemIds",
						MatcherType.ITEM, (e) -> {
							Optional<ItemStack> inHand = e.getCause().first(Player.class).get().getItemInHand();
							return inHand.isPresent() ? inHand.get() : null;
						}))
				.build();
	}
}
