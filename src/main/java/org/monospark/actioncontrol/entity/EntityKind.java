package org.monospark.actioncontrol.entity;

import org.monospark.actioncontrol.entity.matcher.EntityKindMatcher;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;

public final class EntityKind implements EntityKindMatcher {

	private static final EntityKindRegistry REGISTRY = new EntityKindRegistry();
	
	public static EntityKindRegistry getRegistry() {
		return REGISTRY;
	}
	
	private EntityType type;
	
	EntityKind(EntityType type) {
		this.type = type;
	}

	@Override
	public boolean matchesEntity(Entity e) {
		return type == e.getType();
	}
	
	public EntityType getType() {
		return type;
	}
}
