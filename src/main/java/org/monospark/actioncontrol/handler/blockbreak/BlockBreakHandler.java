package org.monospark.actioncontrol.handler.blockbreak;

import java.util.Optional;

import org.monospark.actioncontrol.group.Group;
import org.monospark.actioncontrol.handler.ActionHandler;
import org.monospark.actioncontrol.handler.ActionSettings;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent.Break;

import com.google.gson.GsonBuilder;

public final class BlockBreakHandler extends ActionHandler<ChangeBlockEvent.Break, BlockBreakMatcher> {

	public BlockBreakHandler() {
		super("breakBlock", BlockBreakMatcher.class, ChangeBlockEvent.Break.class);
	}

	@Override
	public void handle(Break event) throws Exception {
		Optional<Player> player = event.getCause().first(Player.class);
		if(!player.isPresent()) {
			return;
		}

		Optional<Group> playerGroup = Group.getRegistry().getGroup(player.get());
		if(!playerGroup.isPresent()) {
			return;
		}
		
		Group group = playerGroup.get();
		Optional<ActionSettings<BlockBreakMatcher>> matcher = group.getActionMatcher(this);
		if(!matcher.isPresent()) {
			return;
		}
		
		boolean matches = matcher.get().getMatcher().matches(player.get().getItemInHand(),
				event.getTransactions().get(0).getOriginal().getState());
		boolean allowed = matcher.get().isAllowed(matches);
		if(!allowed) {
			event.setCancelled(true);
		}
	}

	@Override
	public BlockBreakMatcher uniteMatchers(BlockBreakMatcher m1, BlockBreakMatcher m2) {
		return null;
	}

	@Override
	public void registerMatcherDeserializers(GsonBuilder builder) {
		builder.registerTypeAdapter(BlockBreakMatcher.class, new BlockBreakMatcher.Deserializer());
		builder.registerTypeAdapter(BlockBreakMatcher.ToolSettings.class,
				new BlockBreakMatcher.ToolSettings.Deserializer());
		
	}
}
