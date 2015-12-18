package org.monospark.actioncontrol.entity.matcher;

import org.spongepowered.api.entity.Entity;

public interface EntityKindMatcher {

	boolean matchesEntity(Entity e);
}
