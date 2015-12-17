package org.monospark.actioncontrol.config;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.monospark.actioncontrol.handler.ActionHandler;
import org.monospark.actioncontrol.handler.ActionMatcher;
import org.monospark.actioncontrol.handler.ActionResponse;
import org.monospark.actioncontrol.handler.ActionSettings;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

final class ConfigGroup {

	private Optional<String> parent;
	
	private Map<ActionHandler<?, ?>, ActionSettings<?>> settings;

	private ConfigGroup(Optional<String> parent, Map<ActionHandler<?, ?>, ActionSettings<?>> settings) {
		this.parent = parent;
		this.settings = settings;
	}

	Optional<String> getParent() {
		return parent;
	}

	Map<ActionHandler<?, ?>, ActionSettings<?>> getSettings() {
		return settings;
	}

	
	static final class Deserializer implements JsonDeserializer<ConfigGroup> {

		@Override
		public ConfigGroup deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject object = json.getAsJsonObject();
			JsonElement parentElement = object.get("parent");
			Optional<String> parent = parentElement != null ?
					Optional.of(parentElement.getAsString()) : Optional.empty();
			Map<ActionHandler<?, ?>, ActionSettings<?>> settings =
					new HashMap<ActionHandler<?, ?>, ActionSettings<?>>();
			for (ActionHandler<?,?> handler : ActionHandler.ALL) {
				JsonObject handlerSettings = object.getAsJsonObject(handler.getName());
				if(handlerSettings == null) {
					throw new JsonParseException("Missing \"" + handler.getName() + "\" property");
				}

				JsonElement responseElement = handlerSettings.get("response");
				if(responseElement == null) {
					throw new JsonParseException("Missing \"response\" element");
				}
				ActionResponse response = context.deserialize(responseElement, ActionResponse.class);
				
				JsonElement matcherElement = handlerSettings.get("matcher");
				if(matcherElement == null) {
					throw new JsonParseException("Missing \"matcher\" element");
				}
				ActionMatcher matcher = context.deserialize(matcherElement, handler.getMatcherClass());
				
				settings.put(handler, new ActionSettings<ActionMatcher>(response, matcher));
			}
			return new ConfigGroup(parent, settings);
		}
	}
}
