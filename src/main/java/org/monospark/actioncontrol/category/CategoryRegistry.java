package org.monospark.actioncontrol.category;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.monospark.actioncontrol.config.ConfigParseException;
import org.monospark.actioncontrol.config.ConfigParser;
import org.spongepowered.api.entity.living.player.Player;

public final class CategoryRegistry {

	private static final String PERMISSION_BASE = "actioncontrol.category.";
	
	private Set<Category> allCategories;
	
	CategoryRegistry() {}
	
	public void loadGroups(Path path) throws ConfigParseException {
		Path groupsFile = path.resolve("categories.json");
		File file = groupsFile.toFile();
		try {
			if(!file.exists()) {
				path.toFile().mkdir();
				file.createNewFile();
				this.allCategories = Collections.emptySet();
			} else {
				this.allCategories = ConfigParser.parseConfig(groupsFile);
			}
		} catch (IOException e) {
			throw new ConfigParseException(e);
		}
	}
	
	public Set<Category> getCategories(Player p) {
		return allCategories.stream()
				.filter(c -> /* p.hasPermission(PERMISSION_BASE + c.getName()) */ true)
				.collect(Collectors.toSet());
	}
}
