package org.monospark.actioncontrol.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.monospark.actioncontrol.category.Category;
import org.monospark.actioncontrol.category.Category.MatchType;

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

    private static final Pattern CATEGORY_NAME_REGEX = Pattern.compile("^(?:(except\\((\\w+))\\)|(\\w+))$");
    private static final int EXCEPT_WITH_NAME = 1;
    private static final int EXCEPT_CATEGORY_NAME = 2;
    private static final int SIMPLE_CATEGORY_NAME = 3;

    private static Set<Category> createCategories(Config config) {
        Set<Category> categories = Sets.newHashSet();
        for (Entry<String, ConfigCategory> entry : config.getCategories().entrySet()) {
            Matcher nameMatcher = CATEGORY_NAME_REGEX.matcher(entry.getKey());
            if (!nameMatcher.matches()) {
                throw new JsonParseException("Invalid category name: " + entry.getKey());
            }

            boolean isExcept = nameMatcher.group(EXCEPT_WITH_NAME) != null;
            if (isExcept) {
                categories.add(new Category(nameMatcher.group(EXCEPT_CATEGORY_NAME),
                        MatchType.EXCEPT, entry.getValue().getSettings()));
            } else {
                categories.add(new Category(nameMatcher.group(SIMPLE_CATEGORY_NAME),
                        MatchType.SIMPLE, entry.getValue().getSettings()));
            }
        }
        return categories;
    }
}
