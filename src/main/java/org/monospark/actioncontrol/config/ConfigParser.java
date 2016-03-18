package org.monospark.actioncontrol.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

public final class ConfigParser {

    private static final Gson GSON = createGson();

    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Config.class, new Config.Deserializer())
                .create();
    }

    private ConfigParser() {}

    public static Set<Config> loadConfigs(Path path) throws IOException {
        path.toFile().mkdir();
        Set<Config> configs = Sets.newHashSet();
        for (File file : path.toFile().listFiles()) {
            if (file.isDirectory()) {
                continue;
            }

            try {
                configs.add(parseConfig(file));
            } catch (JsonParseException e) {
                throw new IOException("Error while parsing config file " + file.getName(), e);
            }
        }

        if (configs.size() == 0) {
            createDefaultConfig(path.resolve("example.json"));
        }

        return configs;
    }

    private static void createDefaultConfig(Path target) throws IOException {
        Files.copy(ConfigParser.class.getResourceAsStream("example.json"), target);
    }

    private static Config parseConfig(File configFile) throws IOException {
        String contents = new String(Files.readAllBytes(configFile.toPath()));
        Config config = GSON.fromJson(contents, Config.class);
        return config;
    }
}
