package org.monospark.actioncontrol.matcher.object.item;

import java.util.Map;

import org.monospark.actioncontrol.matcher.Matcher;
import org.monospark.actioncontrol.matcher.object.ObjectMatcherCreator;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public final class ItemMatcherCreator extends ObjectMatcherCreator<ItemKind, ItemStackSnapshot> {

    private boolean acceptHand;

    public ItemMatcherCreator(boolean acceptHand) {
        super(ItemKind.getRegistry());
        this.acceptHand = acceptHand;
    }

    @Override
    protected Matcher<ItemStackSnapshot> createWildcardMatcher() {
        return new Matcher<ItemStackSnapshot>() {

            @Override
            public boolean matches(ItemStackSnapshot o) {
                if (o == null) {
                    return acceptHand;
                }

                return !o.getType().getBlock().isPresent();
            }
        };
    }

    @Override
    protected void addCustomMatchers(Map<String, Matcher<ItemStackSnapshot>> matchers) {
        if (acceptHand) {
            matchers.put("none", new Matcher<ItemStackSnapshot>() {

                @Override
                public boolean matches(ItemStackSnapshot o) {
                    return o == null;
                }
            });
        }
    }
}
