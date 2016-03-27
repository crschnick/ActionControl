package org.monospark.actioncontrol;

import java.io.IOException;
import java.nio.file.Path;

import org.monospark.actioncontrol.config.ConfigRegistry;
import org.monospark.actioncontrol.rule.ActionRule;
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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.google.inject.Inject;

@Plugin(id = "org.monospark.actioncontrol", name = "ActionControl", version = "1.0", description = "Description")
public final class ActionControl {

    private static ActionControl instance = null;

    public static ActionControl getInstance() {
        return instance;
    }

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path privateConfigDir;

    @Inject
    private Logger logger;

    ActionControl() {
        instance = this;
    }

    @Listener
    public void onServerInit(GameInitializationEvent event) {
        Sponge.getCommandManager().register(this, createReloadCommandSpec(), "actioncontrol");

        for (ActionRule<?> rule : ActionRule.ALL) {
            registerActionRule(rule);
        }

        loadConfigs();
    }

    private <E extends Event & Cancellable> void registerActionRule(ActionRule<E> rule) {
        Sponge.getGame().getEventManager().registerListener(this, rule.getEventClass(), rule);
    }

    private CommandSpec createReloadCommandSpec() {
        CommandSpec reloadCommand = CommandSpec.builder()
                .description(Text.of("Reload all ActionControl config files"))
                .permission("actioncontrol.reload")
                .executor(new CommandExecutor() {

            @Override
            public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
                boolean successful = loadConfigs();
                if (src instanceof Player) {
                    Player player = (Player) src;
                    if (successful) {
                        player.sendMessage(Text.builder("Successfully reloaded the config files")
                                .color(TextColors.GREEN)
                                .build());
                    } else {
                        player.sendMessage(Text.builder("An error occured while loading a config file. "
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

    private boolean loadConfigs() {
        try {
            ConfigRegistry.getRegistry().loadConfigs(privateConfigDir);
            logger.info("Successfully loaded the config files");
            return true;
        } catch (IOException e) {
            logger.error("An error occured while loading a config file.", e);
            logger.info("Fix the config and reload the plugin or restart the server to make it work again.");
            return false;
        }
    }

    public Logger getLogger() {
        return logger;
    }
}
