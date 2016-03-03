package org.monospark.actioncontrol.rule;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.monospark.actioncontrol.rule.filter.ActionFilter;
import org.monospark.actioncontrol.rule.filter.ActionFilterOption;
import org.monospark.actioncontrol.rule.filter.ActionFilterTemplate;
import org.monospark.actioncontrol.rule.response.ActionResponse;
import org.monospark.actioncontrol.rule.response.ActionResponseType;
import org.monospark.spongematchers.matcher.SpongeMatcher;
import org.monospark.spongematchers.parser.SpongeMatcherParseException;
import org.monospark.spongematchers.parser.element.StringElement;
import org.monospark.spongematchers.parser.element.StringElementParser;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public final class ActionSettings<E extends Event & Cancellable> {

    private Set<ActionFilter<E>> filters;

    private Set<ActionResponse> matchResponses;

    private Set<ActionResponse> noMatchResponses;

    private ActionSettings(Set<ActionFilter<E>> filters, Set<ActionResponse> matchResponses,
            Set<ActionResponse> noMatchResponses) {
        this.filters = filters;
        this.matchResponses = matchResponses;
        this.noMatchResponses = noMatchResponses;
    }

    public void handleEvent(E event) {
        boolean matchOccured = false;

        for (ActionFilter<E> filter : filters) {
            boolean matches = filter.matches(event);
            if (matches) {
                matchOccured = true;
                break;
            }
        }

        for (ActionResponse response : (matchOccured ? matchResponses : noMatchResponses)) {
            response.execute(event);
        }
    }

    static final class Deserializer<E extends Event & Cancellable> {

        private ActionRule<E> handler;

        Deserializer(ActionRule<E> handler) {
            this.handler = handler;
        }

        public ActionSettings<E> deserialize(JsonElement json) throws JsonParseException, SpongeMatcherParseException {
            JsonObject settingsObject = json.getAsJsonObject();
            JsonElement filterElement = settingsObject.get("filter");
            if (filterElement == null) {
                throw new JsonParseException("Missing \"filter\" property");
            }

            JsonElement responseElement = settingsObject.get("response");
            if (responseElement == null) {
                throw new JsonParseException("Missing \"response\" property");
            }

            JsonElement matchElement = responseElement.getAsJsonObject().get("match");
            Set<ActionResponse> matchResponses = matchElement != null ? deserializeActionResponses(matchElement)
                    : Collections.emptySet();

            JsonElement noMatchElement = responseElement.getAsJsonObject().get("noMatch");
            Set<ActionResponse> noMatchResponses = noMatchElement != null ? deserializeActionResponses(noMatchElement)
                    : Collections.emptySet();

            Set<ActionFilter<E>> filters = deserializeFilters(filterElement, handler.getFilterTemplate());
            return new ActionSettings<E>(filters, matchResponses, noMatchResponses);
        }

        private Set<ActionResponse> deserializeActionResponses(JsonElement json) {
            if (json.isJsonArray()) {
                Set<ActionResponse> responses = new HashSet<ActionResponse>();
                for (JsonElement element : json.getAsJsonArray()) {
                    responses.add(deserializeActionResponse(element));
                }
                return responses;
            } else {
                return Collections.singleton(deserializeActionResponse(json));
            }
        }

        private ActionResponse deserializeActionResponse(JsonElement json) {
            for (ActionResponseType type : ActionResponseType.ALL_TYPES) {
                Optional<ActionResponse> response = type.parse(json.getAsString());
                if (response.isPresent()) {
                    return response.get();
                }
            }

            throw new JsonParseException("Invalid action response: " + json.getAsString());
        }

        private Set<ActionFilter<E>> deserializeFilters(JsonElement json, ActionFilterTemplate template)
                throws SpongeMatcherParseException {
            Set<ActionFilter<E>> filters = new HashSet<ActionFilter<E>>();
            if (json.isJsonArray()) {
                JsonArray array = json.getAsJsonArray();
                for (JsonElement arrayElement : array) {
                    filters.add(deserializeFilter(arrayElement, template));
                }
            } else {
                filters.add(deserializeFilter(json, template));
            }
            return filters;
        }

        private ActionFilter<E> deserializeFilter(JsonElement json, ActionFilterTemplate template)
                throws SpongeMatcherParseException {
            Map<ActionFilterOption<?, E>, SpongeMatcher<?>> optionMatchers =
                    new HashMap<ActionFilterOption<?, E>, SpongeMatcher<?>>();
            JsonObject object = json.getAsJsonObject();
            for (ActionFilterOption<?, ?> option : template.getOptions()) {
                @SuppressWarnings("unchecked")
                ActionFilterOption<?, E> castOption = (ActionFilterOption<?, E>) option;
                JsonElement optionElement = object.get(option.getName());
                if (optionElement == null) {
                    continue;
                }

                String matcherString = optionElement.getAsString();
                StringElement matcherElement = StringElementParser.parseStringElement(matcherString);
                SpongeMatcher<?> matcher = option.getType().parseMatcher(matcherElement);
                optionMatchers.put(castOption, matcher);
            }
            return new ActionFilter<E>(optionMatchers);
        }
    }
}
