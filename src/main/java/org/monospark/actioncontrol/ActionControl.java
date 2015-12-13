package org.monospark.actioncontrol;

import java.nio.file.Path;

import org.monospark.actioncontrol.config.ConfigParseException;
import org.monospark.actioncontrol.group.Group;
import org.monospark.actioncontrol.handler.ActionHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

@Plugin(id = "actionpermissions", name = "ActionPermissions", version = "1.0")
public class ActionControl {
	
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path privateConfigDir;
	
	@Listener
	public void onServerInit(GameInitializationEvent event) {
		for(ActionHandler<?, ?> handler : ActionHandler.ALL) {
			registerActionHandler(handler);
		}
		
		try {
			Group.getRegistry().loadGroups(privateConfigDir);
		} catch (ConfigParseException e) {
			e.printStackTrace();
		}
	}
	
	private <E extends Event> void registerActionHandler(ActionHandler<E, ?> handler) {
		Sponge.getGame().getEventManager().registerListener(this, handler.getEventClass(), handler);
	}
}
