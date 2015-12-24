package org.monospark.actioncontrol.config;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

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
            Map<ActionHandler<?, ?>, ActionSettings> allSettings = new HashMap<ActionHandler<?, ?>, ActionSettings>();
            for (Entry<String, JsonElement> entry : object.entrySet()) {
                Optional<ActionHandler<?, ?>> handler = ActionHandler.byName(entry.getKey());
                if (handler == null) {
                    throw new JsonParseException("Unknown action rule: " + entry.getKey());
                }

                ActionSettings settings = handler.get().deserializeSettings(entry.getValue());
                allSettings.put(handler.get(), settings);
            }
            return new ConfigCategory(allSettings);
        }
    }
}
