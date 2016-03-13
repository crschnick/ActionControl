package org.monospark.actioncontrol.rule.impl;

import java.util.Optional;

import org.monospark.actioncontrol.rule.ActionRule;
import org.monospark.actioncontrol.rule.filter.ActionFilterOption;
import org.monospark.actioncontrol.rule.filter.ActionFilterTemplate;
import org.monospark.spongematchers.type.MatcherType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.item.inventory.ItemStack;

public final class EntityInteractRule<E extends InteractEntityEvent> extends ActionRule<E> {

    public EntityInteractRule(String name, Class<E> eventClass) {
        super(name, eventClass);
    }

    @Override
    protected ActionFilterTemplate createFilter() {
        return ActionFilterTemplate.builder()
                .addOption(new ActionFilterOption<Entity, InteractEntityEvent>("entity",
                        MatcherType.ANY_ENTITY, e -> e.getTargetEntity()))
                .addOption(new ActionFilterOption<Optional<ItemStack>, InteractEntityEvent>("item",
                        MatcherType.optional(MatcherType.ITEM_STACK), (e) -> {
                            Optional<ItemStack> inHand = e.getCause().first(Player.class).get().getItemInHand();
                            return inHand;
                        })).build();
    }

}
