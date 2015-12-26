package org.monospark.actioncontrol.matcher.enchantment;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.monospark.actioncontrol.matcher.Matcher;
import org.monospark.actioncontrol.matcher.MatcherAmount;
import org.monospark.actioncontrol.matcher.MatcherCreator;
import org.spongepowered.api.data.meta.ItemEnchantment;

public final class EnchantmentMatcherCreator extends MatcherCreator<ItemEnchantment> {

    private static final Pattern ENCHANTMENT_NAME_PATTERN = Pattern.compile("(\\w+:)?(\\w+)(\\((\\d+)(-(\\d+)\\))?)?");

    private static final int MOD_PREFIX = 1;
    private static final int NAME = 2;
    private static final int LEVEL_DATA = 3;
    private static final int LEVEL_VALUE = 4;
    private static final int LEVEL_RANGE = 5;
    private static final int LEVEL_RANGE_END = 6;

    @Override
    protected Matcher<ItemEnchantment> createWildcardMatcher() {
        return new Matcher<ItemEnchantment>() {

            @Override
            public boolean matches(ItemEnchantment o) {
                return true;
            }
        };
    }

    @Override
    protected Optional<? extends Matcher<ItemEnchantment>> getNormalMatcher(String name) {
        Optional<String> formattedName = format(name);
        if (!formattedName.isPresent()) {
            return Optional.empty();
        }

        java.util.regex.Matcher matcher = ENCHANTMENT_NAME_PATTERN.matcher(formattedName.get());
        matcher.matches();

        String enchantmentName = matcher.group(NAME);
        if (matcher.group(LEVEL_RANGE) != null) {
            int start = Integer.valueOf(matcher.group(LEVEL_VALUE));
            int end = Integer.valueOf(matcher.group(LEVEL_RANGE_END));
            Set<Matcher<ItemEnchantment>> matchers = new HashSet<Matcher<ItemEnchantment>>();
            for (int i = start; i <= end; i++) {
                Optional<EnchantmentKind> kind = EnchantmentKind.getRegistry().getKind(enchantmentName, i);
                if (!kind.isPresent()) {
                    return Optional.empty();
                }

                matchers.add(kind.get());
            }
            return Optional.of(new MatcherAmount<ItemEnchantment>(matchers));
        } else {
            int level = Integer.valueOf(matcher.group(LEVEL_VALUE));
            return EnchantmentKind.getRegistry().getKind(enchantmentName, level);
        }
    }

    private Optional<String> format(String name) {
        java.util.regex.Matcher matcher = ENCHANTMENT_NAME_PATTERN.matcher(name);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        boolean hasModPrefix = matcher.group(MOD_PREFIX) != null;
        String formattedName = hasModPrefix ? name : "minecraft:" + name;

        boolean hasLevelData = matcher.group(LEVEL_DATA) != null;
        if (!hasLevelData) {
            formattedName = formattedName + "(1)";
        }

        return Optional.of(formattedName);
    }
}
