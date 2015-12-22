package org.monospark.actioncontrol.handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.monospark.actioncontrol.handler.filter.ActionFilter;
import org.monospark.actioncontrol.handler.filter.ActionFilterOption;
import org.monospark.actioncontrol.handler.filter.ActionFilterTemplate;
import org.monospark.actioncontrol.matcher.Matcher;
import org.monospark.actioncontrol.matcher.MatcherAmount;
import org.monospark.actioncontrol.matcher.MatcherType;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class ActionSettingsSimple<E extends Event> extends ActionSettings {

    private ActionResponse response;

    private Set<ActionFilter<E>> filters;

    public ActionSettingsSimple(ActionResponse response, Set<ActionFilter<E>> filters) {
        this.response = response;
        this.filters = filters;
    }

    public boolean isAllowed(E event) {
        boolean matchOccured = false;

        for (ActionFilter<E> filter : filters) {
            boolean matches = filter.matches(event);
            if (matches) {
                matchOccured = true;
                break;
            }
        }

        return matchOccured ? response == ActionResponse.ALLOW : response == ActionResponse.DENY;
    }

    static final class Deserializer<E extends Event & Cancellable> {

        private ActionHandlerSimple<E> handler;

        Deserializer(ActionHandlerSimple<E> handler) {
            this.handler = handler;
        }

        public ActionSettingsSimple<E> deserialize(JsonElement json) {
            JsonObject settingsObject = json.getAsJsonObject();
            JsonElement responseElement = settingsObject.get("response");
            ActionResponse response = new Gson().fromJson(responseElement, ActionResponse.class);
            JsonElement filterElement = settingsObject.get("filter");
            Set<ActionFilter<E>> filters = deserializeFilters(filterElement, handler.getFilterTemplate());
            return new ActionSettingsSimple<E>(response, filters);
        }

        private Set<ActionFilter<E>> deserializeFilters(JsonElement json, ActionFilterTemplate template) {
            Set<ActionFilter<E>> filters = new HashSet<ActionFilter<E>>();
            if (json.isJsonArray()) {
                JsonArray array = json.getAsJsonArray();
                array.forEach(e -> filters.add(deserializeFilter(e, template)));
            } else {
                filters.add(deserializeFilter(json, template));
            }
            return filters;
        }

        private ActionFilter<E> deserializeFilter(JsonElement json, ActionFilterTemplate template) {
            Map<ActionFilterOption<?, E>, Matcher<?>> optionMatchers =
                    new HashMap<ActionFilterOption<?, E>, Matcher<?>>();
            JsonObject object = json.getAsJsonObject();
            for (ActionFilterOption<?, ?> option : template.getOptions()) {
                @SuppressWarnings("unchecked")
                ActionFilterOption<?, E> castOption = (ActionFilterOption<?, E>) option;
                JsonElement optionElement = object.get(option.getName());
                Matcher<?> matcher = deserializeMatcher(optionElement, option.getType());
                optionMatchers.put(castOption, matcher);
            }
            return new ActionFilter<E>(optionMatchers);
        }

        private <T> Matcher<T> deserializeMatcher(JsonElement json, MatcherType<T> type) {
            if (json.isJsonArray()) {
                JsonArray array = json.getAsJsonArray();
                Set<Matcher<T>> matchers = new HashSet<Matcher<T>>();
                for (int i = 0; i < array.size(); i++) {
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
