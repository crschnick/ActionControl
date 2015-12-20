package org.monospark.actioncontrol.config;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.monospark.actioncontrol.handler.ActionHandler;
import org.monospark.actioncontrol.handler.ActionResponse;
import org.monospark.actioncontrol.handler.ActionSettings;
import org.monospark.actioncontrol.handler.filter.ActionFilter;
import org.monospark.actioncontrol.handler.filter.ActionFilterOption;
import org.monospark.actioncontrol.handler.filter.ActionFilterTemplate;
import org.monospark.actioncontrol.matcher.Matcher;
import org.monospark.actioncontrol.matcher.MatcherAmount;
import org.monospark.actioncontrol.matcher.MatcherType;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.CauseTracked;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

final class ConfigCategory {

	private Map<ActionHandler<?>, ActionSettings<?>> settings;

	private ConfigCategory(Map<ActionHandler<?>, ActionSettings<?>> settings) {
		this.settings = settings;
	}

	Map<ActionHandler<?>, ActionSettings<?>> getSettings() {
		return settings;
	}

	
	static final class Deserializer implements JsonDeserializer<ConfigCategory> {

		@Override
		public ConfigCategory deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject object = json.getAsJsonObject();
			Map<ActionHandler<?>, ActionSettings<?>> allSettings =
					new HashMap<ActionHandler<?>, ActionSettings<?>>();
			for(ActionHandler<?> handler : ActionHandler.ALL) {
				JsonElement settingsElement = object.get(handler.getName());
				if(settingsElement == null) {
					continue;
				}
				
				ActionSettings<?> settings = deserializeSettings(settingsElement, handler, context);
				allSettings.put(handler, settings);
			}
			return new ConfigCategory(allSettings);
		}
		
		private <E extends Event & Cancellable & CauseTracked> ActionSettings<E>
				deserializeSettings(JsonElement settingsElement, ActionHandler<E> handler,
						JsonDeserializationContext context) {
			JsonObject settingsObject = settingsElement.getAsJsonObject();
			JsonElement responseElement = settingsObject.get("response");
			ActionResponse response = context.deserialize(responseElement, ActionResponse.class);
			JsonElement filterElement = settingsObject.get("filter");
			Set<ActionFilter<E>> filters = deserializeFilters(filterElement, handler.getFilterTemplate());
			return new ActionSettings<E>(response, filters);
		}
		
		private <E extends Event> Set<ActionFilter<E>> deserializeFilters(JsonElement json,
				ActionFilterTemplate template) {
			final Set<ActionFilter<E>> filters = new HashSet<ActionFilter<E>>();
			if(json.isJsonArray()) {
				JsonArray array = json.getAsJsonArray();
				array.forEach(e -> filters.add(deserializeFilter(e, template)));
			} else {
				filters.add(deserializeFilter(json, template));
			}
			return filters;
		}
		
		private <E extends Event> ActionFilter<E> deserializeFilter(JsonElement json, ActionFilterTemplate template) {
			Map<ActionFilterOption<?, E>, Matcher<?>> optionMatchers =
					new HashMap<ActionFilterOption<?, E>, Matcher<?>>();
			JsonObject object = json.getAsJsonObject();
			for(ActionFilterOption<?, ?> option : template.getOptions()) {
				@SuppressWarnings("unchecked")
				ActionFilterOption<?, E> castOption = (ActionFilterOption<?, E>) option;
				JsonElement optionElement = object.get(option.getName());
				Matcher<?> matcher = deserializeMatcher(optionElement, option.getType());
				optionMatchers.put(castOption, matcher);
			}
			return new ActionFilter<E>(optionMatchers);
		}
		
		private <T> Matcher<T> deserializeMatcher(JsonElement json, MatcherType<T> type) {
			if(json.isJsonArray()) {
				JsonArray array = json.getAsJsonArray();
				Set<Matcher<T>> matchers = new HashSet<Matcher<T>>();
				for(int i = 0; i < array.size(); i++) {
					String name = array.get(i).getAsString();
					Optional<? extends Matcher<T>> matcher = type.getMatcher(name);
					matchers.add(matcher.get());
				}
				return new MatcherAmount<T>(matchers);
			} else {
				String name = json.getAsString();
				return type.getMatcher(name).get();
			}
		}
	}
}
