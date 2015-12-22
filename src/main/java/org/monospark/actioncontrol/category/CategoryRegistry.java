package org.monospark.actioncontrol.category;

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

    public void loadCategories(Path path) throws ConfigParseException {
        allCategories = ConfigParser.loadCategories(path);
    }

    public Set<Category> getCategories(Player p) {
        //If there was an error loading the config file
        if (allCategories == null) {
            return Collections.emptySet();
        }

        return allCategories.stream()
                .filter(c -> p.hasPermission(PERMISSION_BASE + c.getName()))
                .collect(Collectors.toSet());
    }
}
