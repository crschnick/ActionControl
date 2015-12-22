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

import com.google.gson.JsonParseException;

public final class CategoryRegistry {

    private static final String PERMISSION_BASE = "actioncontrol.category.";

    private Set<Category> allCategories;

    CategoryRegistry() {}

    public void loadCategories(Path path) throws ConfigParseException {
        Path categoriesFile = path.resolve("config.json");
        File file = categoriesFile.toFile();
        try {
            if (!file.exists()) {
                path.toFile().mkdir();
                file.createNewFile();
                this.allCategories = Collections.emptySet();
            } else {
                this.allCategories = ConfigParser.parseConfig(categoriesFile);
            }
        } catch (IOException | JsonParseException e) {
            this.allCategories = Collections.emptySet();
            throw new ConfigParseException(e);
        }
    }

    public Set<Category> getCategories(Player p) {
        return allCategories.stream()
                .filter(c -> /* p.hasPermission(PERMISSION_BASE + c.getName()) */ true)
                .collect(Collectors.toSet());
    }
}
