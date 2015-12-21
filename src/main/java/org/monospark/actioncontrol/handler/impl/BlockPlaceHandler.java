package org.monospark.actioncontrol.handler.impl;

import org.monospark.actioncontrol.handler.ActionHandlerSimple;
import org.monospark.actioncontrol.handler.filter.ActionFilterOption;
import org.monospark.actioncontrol.handler.filter.ActionFilterTemplate;
import org.monospark.actioncontrol.matcher.MatcherType;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.event.block.ChangeBlockEvent;

public final class BlockPlaceHandler extends ActionHandlerSimple<ChangeBlockEvent.Place> {

	public BlockPlaceHandler() {
		super("placeBlock", ChangeBlockEvent.Place.class);
	}

	@Override
	protected ActionFilterTemplate createFilter() {
		return ActionFilterTemplate.builder()
				.addOption(new ActionFilterOption<BlockSnapshot, ChangeBlockEvent.Place>("blockIds",
						MatcherType.BLOCK, e -> e.getTransactions().get(0).getFinal()))
				.build();
	}
}
