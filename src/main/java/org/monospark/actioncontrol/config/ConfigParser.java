package org.monospark.actioncontrol.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.monospark.actioncontrol.rule.ActionRule;
import org.monospark.actioncontrol.rule.ActionSettings;
import org.monospark.spongematchers.matcher.SpongeMatcher;
import org.monospark.spongematchers.parser.SpongeMatcherParseException;
import org.monospark.spongematchers.parser.element.StringElementParser;
import org.monospark.spongematchers.type.MatcherType;
import org.spongepowered.api.entity.living.player.Player;

import com.google.common.collect.Sets;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public final class ConfigParser {

    private ConfigParser() {}

    public static Set<Config> loadConfigs(Path path) throws IOException {
        path.toFile().mkdir();
        Set<Config> configs = Sets.newHashSet();
        for (File file : path.toFile().listFiles()) {
            if (file.isDirectory()) {
                continue;
            }

            try {
                configs.add(parseConfig(file.toPath()));
            } catch (IllegalArgumentException e) {
                throw new IOException("Error while parsing config file " + file.getName(), e);
            }
        }

        if (configs.size() == 0) {
            createDefaultConfig(path.resolve("example.hocon"));
            loadConfigs(path);
        }

        return configs;
    }

    private static void createDefaultConfig(Path target) throws IOException {
        Files.copy(ConfigParser.class.getResourceAsStream("example.hocon"), target);
    }

    private static Config parseConfig(Path configFile) throws IOException {
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
                .setPath(configFile)
                .build();
        ConfigurationNode rootNode = loader.load();
        Config config = null;
        try {
            config = deserializeConfig(rootNode);
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
        return config;
    }

    private static Config deserializeConfig(ConfigurationNode value) throws ObjectMappingException, IOException {
        ConfigurationNode playerFilterNode = value.getNode("player-filter");
        if (playerFilterNode.getValue() == null) {
            throw new IOException("Missing \"player-filter\" node");
        }
        if (playerFilterNode.getString() == null) {
            throw new IOException("Invalid \"player-filter\" node");
        }

        SpongeMatcher<Player> playerFilter = null;
        try {
            playerFilter = MatcherType.PLAYER.parseMatcher(
                    StringElementParser.parseStringElement(playerFilterNode.getString()));
        } catch (SpongeMatcherParseException e) {
            throw new IOException("Invalid player filter", e);
        }

        ConfigurationNode rulesNode = value.getNode("action-rules");
        if (rulesNode.getValue() == null) {
            throw new IOException("Missing \"action-rules\" node");
        }

        Map<ActionRule<?>, ActionSettings<?>> ruleSettings = deserializeActionRules(rulesNode);
        return new Config(playerFilter, ruleSettings);
    }

    private static Map<ActionRule<?>, ActionSettings<?>> deserializeActionRules(ConfigurationNode node)
            throws ObjectMappingException, IOException {
        if (node.getChildrenMap().size() == 0) {
            throw new IOException("Invalid \"action-rules\" node: " + node.getValue());
        }

        Map<ActionRule<?>, ActionSettings<?>> ruleSettings = new HashMap<ActionRule<?>, ActionSettings<?>>();
        for (Entry<Object, ? extends ConfigurationNode> entry : node.getChildrenMap().entrySet()) {
            Optional<ActionRule<?>> rule = ActionRule.byName((String) entry.getKey());
            if (!rule.isPresent()) {
                throw new IOException("Unknown action rule: " + entry.getKey());
            }

            ActionSettings<?> settings = rule.get().deserializeSettings(entry.getValue());
            ruleSettings.put(rule.get(), settings);
        }
        return ruleSettings;
    }
}
