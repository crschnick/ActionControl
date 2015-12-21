package org.monospark.actioncontrol.config;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.monospark.actioncontrol.handler.ActionHandler;
import org.monospark.actioncontrol.handler.ActionSettings;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

final class ConfigCategory {

	private Map<ActionHandler<?, ?>, ActionSettings> settings;

	private ConfigCategory(Map<ActionHandler<?, ?>, ActionSettings> settings) {
		this.settings = settings;
	}

	Map<ActionHandler<?, ?>, ActionSettings> getSettings() {
		return settings;
	}
	
	
	static final class Deserializer implements JsonDeserializer<ConfigCategory> {

		@Override
		public ConfigCategory deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject object = json.getAsJsonObject();
			Map<ActionHandler<?, ?>, ActionSettings> allSettings =
					new HashMap<ActionHandler<?, ?>, ActionSettings>();
			for(ActionHandler<?, ?> handler : ActionHandler.ALL) {
				JsonElement settingsElement = object.get(handler.getName());
				if(settingsElement == null) {
					continue;
				}
				
				ActionSettings settings = handler.deserializeSettings(settingsElement);
				allSettings.put(handler, settings);
			}
			return new ConfigCategory(allSettings);
		}
	}
}
