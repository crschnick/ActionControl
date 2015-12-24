package org.monospark.actioncontrol;

import java.nio.file.Path;

import org.monospark.actioncontrol.category.Category;
import org.monospark.actioncontrol.config.ConfigParseException;
import org.monospark.actioncontrol.rules.ActionRule;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import com.google.inject.Inject;

@Plugin(id = "actioncontrol", name = "ActionControl", version = "1.0")
public final class ActionControl {

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path privateConfigDir;

    @Inject
    private Logger logger;

    @Listener
    public void onServerInit(GameInitializationEvent event) {
        Sponge.getCommandDispatcher().register(this, createReloadCommandSpec(), "actioncontrol");

        for (ActionRule<?, ?> rule : ActionRule.ALL) {
            registerActionRule(rule);
        }

        loadConfig();
    }

    private <E extends Event & Cancellable> void registerActionRule(ActionRule<E, ?> rule) {
        Sponge.getGame().getEventManager().registerListener(this, rule.getEventClass(), rule);
    }

    private CommandSpec createReloadCommandSpec() {
        CommandSpec reloadCommand = CommandSpec.builder()
                .description(Texts.of("Reload the ActionControl config"))
                .permission("actioncontrol.reload")
                .executor(new CommandExecutor() {

            @Override
            public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
                boolean successful = loadConfig();
                if (src instanceof Player) {
                    Player player = (Player) src;
                    if (successful) {
                        player.sendMessage(Texts.builder("Successfully reloaded the config file")
                                .color(TextColors.GREEN)
                                .build());
                    } else {
                        player.sendMessage(Texts.builder("An error occured while loading the config. "
                                + "Check the console for details.")
                                .color(TextColors.RED)
                                .build());
                    }
                }
                return CommandResult.success();
            }
        }).build();

        return CommandSpec.builder().child(reloadCommand, "reload").build();
    }

    private boolean loadConfig() {
        try {
            Category.getRegistry().loadCategories(privateConfigDir);
            logger.info("Successfully loaded the config file");
            return true;
        } catch (ConfigParseException e) {
            logger.error("An error occured while loading the config file.", e);
            logger.info("Fix the config and reload the plugin or restart the server to make it work again.");
            return false;
        }
    }
}
