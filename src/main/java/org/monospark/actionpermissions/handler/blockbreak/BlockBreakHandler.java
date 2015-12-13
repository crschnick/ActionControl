package org.monospark.actionpermissions.handler.blockbreak;

import java.util.Optional;

import org.monospark.actionpermissions.group.Group;
import org.monospark.actionpermissions.handler.ActionHandler;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent.Break;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializer;

public final class BlockBreakHandler extends ActionHandler<ChangeBlockEvent.Break, BlockBreakSettings> {

	public BlockBreakHandler() {
		super("breakBlock", BlockBreakSettings.class, ChangeBlockEvent.Break.class);
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
		Optional<BlockBreakSettings> settings = group.getActionSettings(this);
		if(!settings.isPresent()) {
			return;
		}
		
		boolean allowed = settings.get().canBreak(event.getTransactions().get(0).getOriginal().getState());
		if(!allowed) {
			event.setCancelled(true);
		}
	}

	@Override
	public BlockBreakSettings uniteSettings(BlockBreakSettings s1, BlockBreakSettings s2) {
		return new BlockBreakSettings(s1.getResponse(), Sets.union(s1.getMatchers(), s2.getMatchers()));
	}
	
	@Override
	public JsonDeserializer<BlockBreakSettings> getSettingsDeserializer() {
		return new BlockBreakSettings.Deserializer();
	}
}
