package org.monospark.actioncontrol.config;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.spongepowered.api.entity.living.player.Player;

public final class ConfigRegistry {

    private static final ConfigRegistry INSTANCE = new ConfigRegistry();

    public static ConfigRegistry getRegistry() {
        return INSTANCE;
    }

    private Set<Config> allConfigs;

    private ConfigRegistry() {}

    public void loadConfigs() throws IOException {
        allConfigs = ConfigParser.loadConfigs();
    }

    public Set<Config> getConfigs(Player p) {
        //If there was an error loading the config file
        if (allConfigs == null) {
            return Collections.emptySet();
        }

        return allConfigs.stream()
                .filter(c -> c.getPlayerFilter().matches(p))
                .collect(Collectors.toSet());
    }
}
