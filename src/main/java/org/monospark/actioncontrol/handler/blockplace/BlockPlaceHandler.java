package org.monospark.actioncontrol.handler.blockplace;

import java.util.Optional;

import org.monospark.actioncontrol.group.Group;
import org.monospark.actioncontrol.handler.ActionHandler;
import org.monospark.actioncontrol.handler.ActionSettings;
import org.monospark.actioncontrol.kind.matcher.KindMatcher;
import org.monospark.actioncontrol.kind.matcher.KindMatcherAmount;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent.Place;

import com.google.gson.GsonBuilder;

public final class BlockPlaceHandler extends ActionHandler<ChangeBlockEvent.Place, BlockPlaceMatcher> {

	public BlockPlaceHandler() {
		super("placeBlock", BlockPlaceMatcher.class, ChangeBlockEvent.Place.class);
	}

	@Override
	public void handle(Place event) throws Exception {
		Optional<Player> player = event.getCause().first(Player.class);
		if(!player.isPresent()) {
			return;
		}

		Optional<Group> playerGroup = Group.getRegistry().getGroup(player.get());
		if(!playerGroup.isPresent()) {
			return;
		}
		
		Group group = playerGroup.get();
		Optional<ActionSettings<BlockPlaceMatcher>> matcher = group.getActionMatcher(this);
		if(!matcher.isPresent()) {
			return;
		}
		
		boolean matches = matcher.get().getMatcher().matches(event.getTransactions().get(0).getFinal().getState());
		boolean allowed = matcher.get().isAllowed(matches);
		if(!allowed) {
			event.setCancelled(true);
		}
	}

	@Override
	public void registerMatcherDeserializers(GsonBuilder builder) {
		builder.registerTypeAdapter(BlockPlaceMatcher.class, new BlockPlaceMatcher.Deserializer());
	}

	@Override
	public BlockPlaceMatcher uniteMatchers(BlockPlaceMatcher m1, BlockPlaceMatcher m2) {
		KindMatcher newMatcher = KindMatcherAmount.builder()
				.addKindMatcher(m1.getBlockMatcher())
				.addKindMatcher(m2.getBlockMatcher())
				.build();
		
		return new BlockPlaceMatcher(newMatcher);
	}
}
