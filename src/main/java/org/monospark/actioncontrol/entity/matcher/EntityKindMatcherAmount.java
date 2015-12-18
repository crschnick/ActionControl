package org.monospark.actioncontrol.entity.matcher;

import java.util.Set;

import org.spongepowered.api.entity.Entity;

public final class EntityKindMatcherAmount implements EntityKindMatcher {

	private Set<EntityKindMatcher> matchers;
	
	public EntityKindMatcherAmount(Set<EntityKindMatcher> matchers) {
		this.matchers = matchers;
	}
	
	@Override
	public boolean matchesEntity(Entity e) {
		for (EntityKindMatcher m : matchers) {
			if (m.matchesEntity(e)) {
				return true;
			}
		}
		return false;
	}
}
