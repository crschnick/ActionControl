package org.monospark.actioncontrol.handler.blockinteract;

import java.util.Optional;

import org.monospark.actioncontrol.group.Group;
import org.monospark.actioncontrol.handler.ActionHandler;
import org.monospark.actioncontrol.handler.ActionSettings;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.InteractBlockEvent;

import com.google.common.collect.Sets;
import com.google.gson.GsonBuilder;

public final class BlockInteractHandler extends ActionHandler<InteractBlockEvent, BlockInteractMatcher>{

	public BlockInteractHandler() {
		super("interactWithBlock", BlockInteractMatcher.class, InteractBlockEvent.class);
	}

	@Override
	public void handle(InteractBlockEvent event) throws Exception {
		Optional<Player> player = event.getCause().first(Player.class);
		if(!player.isPresent()) {
			return;
		}

		Optional<Group> playerGroup = Group.getRegistry().getGroup(player.get());
		if(!playerGroup.isPresent()) {
			return;
		}
		
		Group group = playerGroup.get();
		Optional<ActionSettings<BlockInteractMatcher>> matcher = group.getActionMatcher(this);
		if(!matcher.isPresent()) {
			return;
		}
		
		boolean matches = matcher.get().getMatcher().matches(player.get().getItemInHand(),
				event.getTargetBlock().getState());
		boolean allowed = matcher.get().isAllowed(matches);
		if(!allowed) {
			event.setCancelled(true);
		}
	}

	@Override
	public BlockInteractMatcher uniteMatchers(BlockInteractMatcher m1, BlockInteractMatcher m2) {
		return new BlockInteractMatcher(Sets.union(m1.getData(), m2.getData()));
	}

	@Override
	public void registerMatcherDeserializers(GsonBuilder builder) {
		builder.registerTypeAdapter(BlockInteractMatcher.class, new BlockInteractMatcher.Deserializer());
		builder.registerTypeAdapter(BlockInteractMatcher.MatcherData.class,
				new BlockInteractMatcher.MatcherData.Deserializer());
	}
}
