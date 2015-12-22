package org.monospark.actioncontrol.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map.Entry;
import java.util.Set;

import org.monospark.actioncontrol.category.Category;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

public final class ConfigParser {

    private static final Gson GSON = createGson();

    private static Gson createGson() {
        GsonBuilder builder = new GsonBuilder().registerTypeAdapter(ConfigCategory.class,
                new ConfigCategory.Deserializer()).registerTypeAdapter(Config.class, new Config.Deserializer());
        return builder.create();
    }

    private ConfigParser() {}

    public static Set<Category> loadCategories(Path path) throws ConfigParseException {
        Path categoriesFile = path.resolve("config.json");
        try {
            if (!categoriesFile.toFile().exists()) {
                createDefaultConfig(categoriesFile);
            }
            return ConfigParser.parseConfig(categoriesFile);
        } catch (IOException | JsonParseException e) {
            throw new ConfigParseException(e);
        }
    }

    private static void createDefaultConfig(Path target) throws IOException {
        Files.copy(ConfigParser.class.getResourceAsStream("config.json"), target);
    }

    private static Set<Category> parseConfig(Path configFile) throws IOException {
        String contents = new String(Files.readAllBytes(configFile));
        Config config = GSON.fromJson(contents, Config.class);
        return createCategories(config);
    }

    private static Set<Category> createCategories(Config config) {
        Set<Category> categories = Sets.newHashSet();
        for (Entry<String, ConfigCategory> entry : config.getCategories().entrySet()) {
            categories.add(new Category(entry.getKey(), entry.getValue().getSettings()));
        }
        return categories;
    }
}
