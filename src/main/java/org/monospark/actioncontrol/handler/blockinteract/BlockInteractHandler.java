package org.monospark.actioncontrol.handler.blockinteract;

import org.monospark.actioncontrol.handler.ActionHandler;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.InteractBlockEvent;

import com.google.gson.GsonBuilder;

public final class BlockInteractHandler extends ActionHandler<InteractBlockEvent, BlockInteractMatcher>{

	public BlockInteractHandler() {
		super("interactWithBlock", BlockInteractMatcher.class, InteractBlockEvent.class);
	}
	
	@Override
	protected boolean matches(InteractBlockEvent event, Player p, BlockInteractMatcher matcher) {
		return matcher.matches(p.getItemInHand(), event.getTargetBlock().getState());
	}

	@Override
	public void registerMatcherDeserializers(GsonBuilder builder) {
		builder.registerTypeAdapter(BlockInteractMatcher.class, new BlockInteractMatcher.Deserializer());
		builder.registerTypeAdapter(BlockInteractMatcher.MatcherData.class,
				new BlockInteractMatcher.MatcherData.Deserializer());
	}

}
