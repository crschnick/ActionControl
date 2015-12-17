package org.monospark.actioncontrol.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map.Entry;
import java.util.Set;

import org.monospark.actioncontrol.group.Category;
import org.monospark.actioncontrol.handler.ActionHandler;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class ConfigParser {

	private static final Gson GSON = createGson();
	
	private static Gson createGson() {
		GsonBuilder builder = new GsonBuilder()
				.registerTypeAdapter(ConfigCategory.class, new ConfigCategory.Deserializer())
				.registerTypeAdapter(Config.class, new Config.Deserializer());
		for(ActionHandler<?, ?> handler : ActionHandler.ALL) {
			handler.registerMatcherDeserializers(builder);
		}
		return builder.create();
	}
	
	private ConfigParser() {}
	
	public static Set<Category> parseConfig(Path configFile) throws IOException {
		String contents = new String(Files.readAllBytes(configFile));
		Config config = GSON.fromJson(contents, Config.class);
		return createCategories(config);
	}

	private static Set<Category> createCategories(Config config) {
		Set<Category> categories = Sets.newHashSet();
		for(Entry<String, ConfigCategory> entry : config.getCategories().entrySet()) {
			categories.add(new Category(entry.getKey(), entry.getValue().getSettings()));
		}
		return categories;
	}
}
