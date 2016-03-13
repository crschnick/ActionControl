package org.monospark.actioncontrol.config;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.monospark.actioncontrol.rule.ActionRule;
import org.monospark.actioncontrol.rule.ActionSettings;
import org.monospark.spongematchers.matcher.SpongeMatcher;
import org.monospark.spongematchers.parser.SpongeMatcherParseException;
import org.monospark.spongematchers.parser.element.StringElementParser;
import org.monospark.spongematchers.type.MatcherType;
import org.spongepowered.api.entity.living.player.Player;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public final class Config {

    private SpongeMatcher<Player> playerFilter;

    private Map<ActionRule<?>, ActionSettings<?>> settings;

    private Config(SpongeMatcher<Player> playerFilter, Map<ActionRule<?>, ActionSettings<?>> settings) {
        this.playerFilter = playerFilter;
        this.settings = settings;
    }

    public SpongeMatcher<Player> getPlayerFilter() {
        return playerFilter;
    }

    public Map<ActionRule<?>, ActionSettings<?>> getSettings() {
        return settings;
    }


    static final class Deserializer implements JsonDeserializer<Config> {

        @Override
        public Config deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonElement playerFilterElement = json.getAsJsonObject().get("playerFilter");
            if (playerFilterElement == null) {
                throw new JsonParseException("Missing \"playerFilter\" property");
            }

            SpongeMatcher<Player> playerFilter = null;
            try {
                playerFilter = MatcherType.PLAYER.parseMatcher(
                        StringElementParser.parseStringElement(playerFilterElement.getAsString()));
            } catch (SpongeMatcherParseException e) {
                throw new JsonParseException(e);
            }

            JsonElement rulesElement = json.getAsJsonObject().get("actionRules");
            if (rulesElement == null) {
                throw new JsonParseException("Missing \"actionRules\" property");
            }

            Map<ActionRule<?>, ActionSettings<?>> ruleSettings = deserializeRuleSettings(rulesElement);
            return new Config(playerFilter, ruleSettings);
        }

        private Map<ActionRule<?>, ActionSettings<?>> deserializeRuleSettings(JsonElement json)
                throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            Map<ActionRule<?>, ActionSettings<?>> ruleSettings = new HashMap<ActionRule<?>, ActionSettings<?>>();
            for (Entry<String, JsonElement> entry : object.entrySet()) {
                Optional<ActionRule<?>> rule = ActionRule.byName(entry.getKey());
                if (rule == null) {
                    throw new JsonParseException("Unknown action rule: " + entry.getKey());
                }

                ActionSettings<?> settings = null;
                try {
                    settings = rule.get().deserializeSettings(entry.getValue());
                } catch (SpongeMatcherParseException e) {
                    throw new JsonParseException("Can't parse action settings for rule: " + entry.getKey(), e);
                }
                ruleSettings.put(rule.get(), settings);
            }
            return ruleSettings;
        }
    }
}
