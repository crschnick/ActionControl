package org.monospark.actioncontrol.rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.monospark.actioncontrol.matcher.Matcher;
import org.monospark.actioncontrol.matcher.MatcherAmount;
import org.monospark.actioncontrol.matcher.MatcherType;
import org.monospark.actioncontrol.rule.filter.ActionFilter;
import org.monospark.actioncontrol.rule.filter.ActionFilterOption;
import org.monospark.actioncontrol.rule.filter.ActionFilterTemplate;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

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

        private ActionRuleSimple<E> handler;

        Deserializer(ActionRuleSimple<E> handler) {
            this.handler = handler;
        }

        public ActionSettingsSimple<E> deserialize(JsonElement json) throws JsonParseException {
            JsonObject settingsObject = json.getAsJsonObject();
            JsonElement responseElement = settingsObject.get("response");
            if (responseElement == null) {
                throw new JsonParseException("Missing \"response\" property");
            }

            ActionResponse response = new Gson().fromJson(responseElement, ActionResponse.class);
            JsonElement filterElement = settingsObject.get("filter");
            if (filterElement == null) {
                throw new JsonParseException("Missing \"filter\" property");
            }

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
                    matchers.add(getMatcherByName(name, type));
                }
                return new MatcherAmount<T>(matchers);
            } else {
                String name = json.getAsString();
                return getMatcherByName(name, type);
            }
        }

        private <T> Matcher<T> getMatcherByName(String name, MatcherType<T> type) {
            Optional<? extends Matcher<T>> matcher = type.getMatcher(name);
            if (!matcher.isPresent()) {
                throw new JsonParseException("Invalid " + type.getName() + " id: " + name);
            }

            return matcher.get();
        }
    }
}
