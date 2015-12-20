package org.monospark.actioncontrol.handler.impl;

import java.util.Optional;

import org.monospark.actioncontrol.handler.ActionHandler;
import org.monospark.actioncontrol.handler.filter.ActionFilterOption;
import org.monospark.actioncontrol.handler.filter.ActionFilterTemplate;
import org.monospark.actioncontrol.matcher.MatcherType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.item.inventory.ItemStack;

public final class EntityInteractHandler<E extends InteractEntityEvent> extends ActionHandler<E> {

	public EntityInteractHandler(String name, Class<E> eventClass) {
		super(name, eventClass);
	}

	@Override
	protected ActionFilterTemplate createFilter() {
		return ActionFilterTemplate.builder()
				.addOption(new ActionFilterOption<Entity, InteractEntityEvent>("entityIds",
						MatcherType.ENTITY, e -> e.getTargetEntity()))
				.addOption(new ActionFilterOption<ItemStack, InteractEntityEvent>("itemIds",
						MatcherType.ITEM, (e) -> {
							Optional<ItemStack> inHand = e.getCause().first(Player.class).get().getItemInHand();
							return inHand.isPresent() ? inHand.get() : null;
						}))
				.build();
	}

}
