package org.monospark.actioncontrol.matcher;

import java.util.Optional;

import org.monospark.actioncontrol.matcher.enchantment.EnchantmentKindRegistry;
import org.monospark.actioncontrol.matcher.entity.EntityKindRegistry;
import org.monospark.actioncontrol.matcher.object.all.AllKindRegistry;
import org.monospark.actioncontrol.matcher.object.block.BlockKindRegistry;
import org.monospark.actioncontrol.matcher.object.item.ItemKindRegistry;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public final class MatcherType<T> {

    public static final MatcherType<BlockSnapshot> BLOCK = new MatcherType<>("block", new BlockKindRegistry());

    public static final MatcherType<ItemStackSnapshot> ITEM = new MatcherType<>("item", new ItemKindRegistry());

    public static final MatcherType<ItemStackSnapshot> OBJECT = new MatcherType<>("object", new AllKindRegistry());

    public static final MatcherType<EntitySnapshot> ENTITY = new MatcherType<>("entity", new EntityKindRegistry());

    public static final MatcherType<ItemEnchantment> ENCHANTMENT = new MatcherType<>("enchantment",
            new EnchantmentKindRegistry());

    private String name;

    private MatcherCreator<T> creator;

    private MatcherType(String name, MatcherCreator<T> creator) {
        this.name = name;
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public Matcher<T> getDefaultMatcher() {
        return creator.getWildcardMatcher();
    }

    public Optional<? extends Matcher<T>> getMatcher(String name) {
        return creator.getMatcher(name);
    }
}
