package org.monospark.actioncontrol.handler.blockplace;

import org.monospark.actioncontrol.handler.ActionHandler;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent.Place;

import com.google.gson.GsonBuilder;

public final class BlockPlaceHandler extends ActionHandler<ChangeBlockEvent.Place, BlockPlaceMatcher> {

	public BlockPlaceHandler() {
		super("placeBlock", BlockPlaceMatcher.class, ChangeBlockEvent.Place.class);
	}

	@Override
	protected boolean matches(Place event, Player p, BlockPlaceMatcher matcher) {
		return matcher.matches(event.getTransactions().get(0).getFinal().getState());
	}
	
	@Override
	public void registerMatcherDeserializers(GsonBuilder builder) {
		builder.registerTypeAdapter(BlockPlaceMatcher.class, new BlockPlaceMatcher.Deserializer());
	}
}
