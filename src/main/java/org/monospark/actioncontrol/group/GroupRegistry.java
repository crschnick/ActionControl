package org.monospark.actioncontrol.group;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.monospark.actioncontrol.config.ConfigParseException;
import org.monospark.actioncontrol.config.ConfigParser;
import org.spongepowered.api.entity.living.player.Player;

public final class GroupRegistry {

	private static final String PERMISSION_BASE = "actioncontrol.group.";
	
	private Set<Group> allGroups;
	
	GroupRegistry() {}
	
	public void loadGroups(Path path) throws ConfigParseException {
		Path groupsFile = path.resolve("groups.json");
		File file = groupsFile.toFile();
		try {
			if(!file.exists()) {
				path.toFile().mkdir();
				file.createNewFile();
				this.allGroups = Collections.emptySet();
			} else {
				this.allGroups = ConfigParser.parseConfig(groupsFile);
			}
		} catch (IOException e) {
			throw new ConfigParseException(e);
		}
	}
	
	public Optional<Group> getGroup(Player p) {
		Group playerGroup = null;
		for(Group g : allGroups) {
			if(true) {//if(p.hasPermission(PERMISSION_BASE + g.getName())) {
				if(playerGroup != null) {
					throw new IllegalArgumentException("Player can't be member of more than one group");
				} else {
					playerGroup = g;
				}
			}
		}
		return Optional.ofNullable(playerGroup);
	}
}
