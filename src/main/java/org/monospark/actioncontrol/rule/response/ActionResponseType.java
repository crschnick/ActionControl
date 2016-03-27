package org.monospark.actioncontrol.rule.response;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

public abstract class ActionResponseType {

    public static final Set<ActionResponseType> ALL_TYPES = createAllResponseTypes();

    private static Set<ActionResponseType> createAllResponseTypes() {
        Set<ActionResponseType> allTypes = new HashSet<ActionResponseType>();
        allTypes.add(new ActionResponseType() {

            @Override
            public Optional<ActionResponse> parse(String string) {
                return string.equalsIgnoreCase("deny") ? Optional.of(new ActionResponse() {
                    @Override
                    public <E extends Event & Cancellable> void execute(E event) {
                        event.setCancelled(true);
                    }
                }) : Optional.empty();
            }
        });

        allTypes.add(new ActionResponseType() {

            private Pattern pattern = Pattern.compile("command\\s*\\((?<command>.+)\\)");

            @Override
            public Optional<ActionResponse> parse(String string) {
                Matcher matcher = pattern.matcher(string);
                if (!matcher.matches()) {
                    return Optional.empty();
                }

                String command = matcher.group("command");
                return Optional.of(new ActionResponse() {
                    @Override
                    public <E extends Event & Cancellable> void execute(E event) {
                        Sponge.getGame().getCommandManager().process(Sponge.getServer().getConsole(),
                                command.replace("<player>", event.getCause().first(Player.class).get().getName()));
                    }
                });
            }
        });

        allTypes.add(new ActionResponseType() {

            private Pattern pattern = Pattern.compile("player-command\\s*\\((?<command>.+)\\)");

            @Override
            public Optional<ActionResponse> parse(String string) {
                Matcher matcher = pattern.matcher(string);
                if (!matcher.matches()) {
                    return Optional.empty();
                }

                String command = matcher.group("command");
                return Optional.of(new ActionResponse() {
                    @Override
                    public <E extends Event & Cancellable> void execute(E event) {
                        Sponge.getGame().getCommandManager().process(event.getCause().first(Player.class).get(),
                                command);
                    }
                });
            }
        });

        allTypes.add(new ActionResponseType() {

            private Pattern pattern = Pattern.compile("log\\s*\\((?<message>.+)\\)");

            @Override
            public Optional<ActionResponse> parse(String string) {
                Matcher matcher = pattern.matcher(string);
                if (!matcher.matches()) {
                    return Optional.empty();
                }

                String message = matcher.group("message");
                return Optional.of(new ActionResponse() {
                    @Override
                    public <E extends Event & Cancellable> void execute(E event) {
                        System.out.println(message);
                    }
                });
            }
        });

        return allTypes;
    }

    public abstract Optional<ActionResponse> parse(String string);
}
