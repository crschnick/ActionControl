package org.monospark.actioncontrol.matcher.entity;

import java.util.Optional;
import java.util.regex.Pattern;

import org.monospark.actioncontrol.matcher.Matcher;
import org.monospark.actioncontrol.matcher.MatcherCreator;
import org.spongepowered.api.entity.EntitySnapshot;

public final class EntityMatcherCreator extends MatcherCreator<EntitySnapshot> {

    private static final Pattern ENTITY_NAME_PATTERN = Pattern.compile("(\\w+:)?(\\w+)");

    private static final int MOD_PREFIX = 1;

    @Override
    protected Matcher<EntitySnapshot> createWildcardMatcher() {
        return new Matcher<EntitySnapshot>() {

            @Override
            public boolean matches(EntitySnapshot e) {
                return true;
            }
        };
    }

    @Override
    protected Optional<? extends Matcher<EntitySnapshot>> getNormalMatcher(String name) {
        Optional<String> formattedName = format(name);
        if (!formattedName.isPresent()) {
            return Optional.empty();
        }

        return EntityKind.getRegistry().getKind(formattedName.get());
    }

    private Optional<String> format(String name) {
        java.util.regex.Matcher matcher = ENTITY_NAME_PATTERN.matcher(name);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        boolean hasModPrefix = matcher.group(MOD_PREFIX) != null;
        return Optional.of(hasModPrefix ? name : "minecraft:" + name);
    }
}
