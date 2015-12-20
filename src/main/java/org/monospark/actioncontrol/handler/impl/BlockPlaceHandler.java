package org.monospark.actioncontrol.handler.impl;

import org.monospark.actioncontrol.handler.ActionHandler;
import org.monospark.actioncontrol.handler.filter.ActionFilterTemplate;
import org.monospark.actioncontrol.handler.filter.ActionFilterOption;
import org.monospark.actioncontrol.matcher.MatcherType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.event.block.ChangeBlockEvent;

public final class BlockPlaceHandler extends ActionHandler<ChangeBlockEvent.Place> {

	public BlockPlaceHandler() {
		super("placeBlock", ChangeBlockEvent.Place.class);
	}

	@Override
	protected ActionFilterTemplate createFilter() {
		return ActionFilterTemplate.builder()
				.addOption(new ActionFilterOption<BlockState, ChangeBlockEvent.Place>("blockIds",
						MatcherType.BLOCK, e -> e.getTransactions().get(0).getFinal().getState()))
				.build();
	}
}
