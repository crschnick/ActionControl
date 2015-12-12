package org.monospark.actionpermissions.config;

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
	
	private Map<String, ConfigGroup> groups;

	private Config(Map<String, ConfigGroup> groups) {
		this.groups = groups;
	}

	Map<String, ConfigGroup> getGroups() {
		return groups;
	}

	static final class Deserializer implements JsonDeserializer<Config> {

		@Override
		public Config deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject object = json.getAsJsonObject().getAsJsonObject("groups");
			if(object == null) {
				throw new JsonParseException("Missing \"groups\" property");
			}
			
			Map<String, ConfigGroup> groups = new HashMap<String, ConfigGroup>();
			for(Entry<String,JsonElement> entry : object.entrySet()) {
				JsonObject groupObject = entry.getValue().getAsJsonObject();
				ConfigGroup group = context.deserialize(groupObject, ConfigGroup.class);
				groups.put(entry.getKey(), group);
			}
			return new Config(groups);
		}
		
	}
}
