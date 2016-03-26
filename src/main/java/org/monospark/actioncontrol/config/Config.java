package org.monospark.actioncontrol.config;

import java.util.Map;

import org.monospark.actioncontrol.rule.ActionRule;
import org.monospark.actioncontrol.rule.ActionSettings;
import org.monospark.spongematchers.matcher.SpongeMatcher;
import org.spongepowered.api.entity.living.player.Player;

public final class Config {

    private SpongeMatcher<Player> playerFilter;

    private Map<ActionRule<?>, ActionSettings<?>> settings;

    Config(SpongeMatcher<Player> playerFilter, Map<ActionRule<?>, ActionSettings<?>> settings) {
        this.playerFilter = playerFilter;
        this.settings = settings;
    }

    public SpongeMatcher<Player> getPlayerFilter() {
        return playerFilter;
    }

    public Map<ActionRule<?>, ActionSettings<?>> getSettings() {
        return settings;
    }
}
