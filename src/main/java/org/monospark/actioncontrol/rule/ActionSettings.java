package org.monospark.actioncontrol.rule;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

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

        public ActionSettings<E> deserialize(ConfigurationNode node) throws ObjectMappingException, IOException {
            ConfigurationNode filterNode = node.getNode("filter");
            if (filterNode.getValue() == null) {
                throw new IOException("Missing \"filter\" property");
            }

            ConfigurationNode responseNode = node.getNode("response");
            if (responseNode.getValue() == null) {
                throw new IOException("Missing \"response\" property");
            }

            ConfigurationNode matchNode = responseNode.getNode("match");
            Set<ActionResponse> matchResponses = matchNode.getValue() != null ? deserializeActionResponses(matchNode)
                    : Collections.emptySet();

            ConfigurationNode noMatchNode = responseNode.getNode("noMatch");
            Set<ActionResponse> noMatchResponses = noMatchNode.getValue() != null
                    ? deserializeActionResponses(noMatchNode) : Collections.emptySet();

            Set<ActionFilter<E>> filters = deserializeFilters(filterNode, handler.getFilterTemplate());
            return new ActionSettings<E>(filters, matchResponses, noMatchResponses);
        }

        private Set<ActionResponse> deserializeActionResponses(ConfigurationNode node) throws ObjectMappingException,
                IOException {
            List<String> list = node.getList(TypeToken.of(String.class));
            if (list.size() == 0) {
                throw new IOException("Invalid action response: " + node.getValue());
            }

            Set<ActionResponse> responses = new LinkedHashSet<ActionResponse>();
            for (String string : list) {
                responses.add(parseActionResponse(string));
            }
            return responses;
        }

        private ActionResponse parseActionResponse(String string) throws IOException {
            for (ActionResponseType type : ActionResponseType.ALL_TYPES) {
                Optional<ActionResponse> response = type.parse(string);
                if (response.isPresent()) {
                    return response.get();
                }
            }

            throw new IOException("Invalid action response: " + string);
        }

        /**
         * Deserializes {@code filter} nodes. Filter nodes can either be a single node or a list of nodes.
         */
        private Set<ActionFilter<E>> deserializeFilters(ConfigurationNode node, ActionFilterTemplate template)
                throws IOException, ObjectMappingException {
            Map<Object, ? extends ConfigurationNode> map = node.getChildrenMap();
            List<? extends ConfigurationNode> list = node.getChildrenList();

            if (map.size() == 0 && list.size() == 0) {
                throw new IOException("Invalid filter: " + node.getValue());
            }

            if (map.size() > 0) {
                return Collections.singleton(deserializeFilter(map, template));
            } else {
                Set<ActionFilter<E>> filters = new HashSet<ActionFilter<E>>();
                for (ConfigurationNode filterNode : list) {
                    filters.add(deserializeFilter(filterNode.getChildrenMap(), template));
                }
                return filters;
            }
        }

        /**
         * Creates a single action filter from a map of filter options.
         */
        private ActionFilter<E> deserializeFilter(Map<Object, ? extends ConfigurationNode> map,
                ActionFilterTemplate template) throws IOException {
            Map<ActionFilterOption<?, E>, SpongeMatcher<?>> optionMatchers =
                    new HashMap<ActionFilterOption<?, E>, SpongeMatcher<?>>();

            //Check if all filter options exist
            for (Entry<Object, ? extends ConfigurationNode> entry : map.entrySet()) {
                boolean validOption = template.getOptions().stream()
                        .map(o -> o.getName())
                        .anyMatch(s -> entry.getKey().equals(s));
                if (!validOption) {
                    throw new IOException("Invalid filter option: " + entry.getKey());
                }
            }

            //Parse the matchers for all filter options
            for (ActionFilterOption<?, ?> option : template.getOptions()) {
                @SuppressWarnings("unchecked")
                ActionFilterOption<?, E> castOption = (ActionFilterOption<?, E>) option;
                ConfigurationNode optionNode = map.get(option.getName());
                if (optionNode == null) {
                    continue;
                }

                String matcherString = optionNode.getString();
                if (matcherString == null) {
                    throw new IOException("Invalid filter option: " + optionNode.getValue());
                }

                try {
                    StringElement matcherElement = StringElementParser.parseStringElement(matcherString);
                    SpongeMatcher<?> matcher = option.getType().parseMatcher(matcherElement);
                    optionMatchers.put(castOption, matcher);
                } catch (SpongeMatcherParseException e) {
                    throw new IOException("Invalid matcher: " + matcherString, e);
                }
            }
            return new ActionFilter<E>(optionMatchers);
        }
    }
}
