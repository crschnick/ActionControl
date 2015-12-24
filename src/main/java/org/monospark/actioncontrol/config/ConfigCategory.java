package org.monospark.actioncontrol.config;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.monospark.actioncontrol.rule.ActionRule;
import org.monospark.actioncontrol.rule.ActionSettings;

import java.util.Optional;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

final class ConfigCategory {

    private Map<ActionRule<?, ?>, ActionSettings> settings;

    private ConfigCategory(Map<ActionRule<?, ?>, ActionSettings> settings) {
        this.settings = settings;
    }

    Map<ActionRule<?, ?>, ActionSettings> getSettings() {
        return settings;
    }

    static final class Deserializer implements JsonDeserializer<ConfigCategory> {

        @Override
        public ConfigCategory deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            Map<ActionRule<?, ?>, ActionSettings> allSettings = new HashMap<ActionRule<?, ?>, ActionSettings>();
            for (Entry<String, JsonElement> entry : object.entrySet()) {
                Optional<ActionRule<?, ?>> rule = ActionRule.byName(entry.getKey());
                if (rule == null) {
                    throw new JsonParseException("Unknown action rule: " + entry.getKey());
                }

                ActionSettings settings = rule.get().deserializeSettings(entry.getValue());
                allSettings.put(rule.get(), settings);
            }
            return new ConfigCategory(allSettings);
        }
    }
}
