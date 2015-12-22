package org.monospark.actioncontrol.handler.impl;

import java.util.Optional;

import org.monospark.actioncontrol.handler.ActionHandlerSimple;
import org.monospark.actioncontrol.handler.filter.ActionFilterOption;
import org.monospark.actioncontrol.handler.filter.ActionFilterTemplate;
import org.monospark.actioncontrol.matcher.MatcherType;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public final class BlockInteractHandler<E extends InteractBlockEvent> extends ActionHandlerSimple<E> {

    public BlockInteractHandler(String name, Class<E> eventClass) {
        super(name, eventClass);
    }

    @Override
    protected ActionFilterTemplate createFilter() {
        return ActionFilterTemplate.builder()
                .addOption(new ActionFilterOption<BlockSnapshot, InteractBlockEvent>("blockIds",
                        MatcherType.BLOCK, e -> e.getTargetBlock()))
                .addOption(new ActionFilterOption<ItemStackSnapshot, InteractBlockEvent>("itemIds",
                        MatcherType.ITEM, (e) -> {
                                Optional<ItemStack> inHand = e.getCause().first(Player.class).get().getItemInHand();
                                return inHand.isPresent() ? inHand.get().createSnapshot() : null;
                        })).build();
    }
}
