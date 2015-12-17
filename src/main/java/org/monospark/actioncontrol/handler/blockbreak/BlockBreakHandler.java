package org.monospark.actioncontrol.handler.blockbreak;

import org.monospark.actioncontrol.handler.ActionHandler;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent.Break;

import com.google.gson.GsonBuilder;

public final class BlockBreakHandler extends ActionHandler<ChangeBlockEvent.Break, BlockBreakMatcher> {

	public BlockBreakHandler() {
		super("breakBlock", BlockBreakMatcher.class, ChangeBlockEvent.Break.class);
	}

	@Override
	protected boolean matches(Break event, Player p, BlockBreakMatcher matcher) {
		return matcher.matches(p.getItemInHand(), event.getTransactions().get(0).getOriginal().getState());
	}

	@Override
	public void registerMatcherDeserializers(GsonBuilder builder) {
		builder.registerTypeAdapter(BlockBreakMatcher.class, new BlockBreakMatcher.Deserializer());
		builder.registerTypeAdapter(BlockBreakMatcher.MatcherData.class,
				new BlockBreakMatcher.MatcherData.Deserializer());
		
	}
}
