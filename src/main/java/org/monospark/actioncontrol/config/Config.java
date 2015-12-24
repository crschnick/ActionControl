package org.monospark.actioncontrol.config;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

final class Config {

    private Map<String, ConfigCategory> categories;

    private Config(Map<String, ConfigCategory> categories) {
        this.categories = categories;
    }

    Map<String, ConfigCategory> getCategories() {
        return categories;
    }

    static final class Deserializer implements JsonDeserializer<Config> {

        @Override
        public Config deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject object = json.getAsJsonObject().getAsJsonObject("categories");
            if (object == null) {
                throw new JsonParseException("Missing \"categories\" property");
            }

            Map<String, ConfigCategory> categories = new HashMap<String, ConfigCategory>();
            for (Entry<String, JsonElement> entry : object.entrySet()) {
                JsonObject categoryObject = entry.getValue().getAsJsonObject();
                ConfigCategory category = context.deserialize(categoryObject, ConfigCategory.class);
                categories.put(entry.getKey(), category);
            }
            return new Config(categories);
        }
    }
}
