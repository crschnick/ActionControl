package org.monospark.actionpermissions.handler.blockplace;

import java.util.Optional;

import org.monospark.actionpermissions.group.Group;
import org.monospark.actionpermissions.handler.ActionHandler;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent.Place;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializer;

public final class BlockPlaceHandler extends ActionHandler<ChangeBlockEvent.Place, BlockPlaceSettings> {

	public BlockPlaceHandler() {
		super("placeBlock", BlockPlaceSettings.class, ChangeBlockEvent.Place.class);
	}

	@Override
	public void handle(Place event) throws Exception {
		Optional<Player> player = event.getCause().first(Player.class);
		if(player.isPresent()) {
			Optional<Group> playerGroup = Group.getRegistry().getGroup(player.get());
			if(playerGroup.isPresent()) {
				Group group = playerGroup.get();
				BlockPlaceSettings settings = group.getActionSettings(this);
				boolean allowed = settings.canPlace(event.getTransactions().get(0).getFinal().getState());
				if(!allowed) {
					event.setCancelled(true);
				}
			}
		}
	}

	@Override
	public BlockPlaceSettings uniteSettings(BlockPlaceSettings s1, BlockPlaceSettings s2) {
		return new BlockPlaceSettings(s1.getResponse(), Sets.union(s1.getMatchers(), s2.getMatchers()));
	}
	
	@Override
	public JsonDeserializer<BlockPlaceSettings> getSettingsDeserializer() {
		return new BlockPlaceSettings.Deserializer();
	}
}
