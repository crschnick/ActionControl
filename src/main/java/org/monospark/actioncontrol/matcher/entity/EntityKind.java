package org.monospark.actioncontrol.matcher.entity;

import org.monospark.actioncontrol.matcher.Matcher;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;

public final class EntityKind implements Matcher<Entity> {

	private static final EntityKindRegistry REGISTRY = new EntityKindRegistry();
	
	public static EntityKindRegistry getRegistry() {
		return REGISTRY;
	}
	
	private EntityType type;
	
	EntityKind(EntityType type) {
		this.type = type;
	}

	@Override
	public boolean matches(Entity e) {
		return type == e.getType();
	}
	
	public EntityType getType() {
		return type;
	}
}
