package org.monospark.actioncontrol.entity;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.monospark.actioncontrol.entity.matcher.EntityKindMatcher;
import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;

public final class EntityKindRegistry {

	private static final Pattern ENTITY_NAME_PATTERN = Pattern.compile("((\\w:)(\\w)");
	
	private static final int MOD_PREFIX = 1;
	
	private static final EntityKindMatcher WILDCARD = new EntityKindMatcher() {
		
		@Override
		public boolean matchesEntity(Entity e) {
			return true;
		}
	};
	
	private boolean init;
	
	private Set<EntityKind> allKinds;
	
	EntityKindRegistry() {
		allKinds = new HashSet<EntityKind>();
	}
	
	private void init() {
		for(EntityType type : Sponge.getRegistry().getAllOf(CatalogTypes.ENTITY_TYPE)) {
			allKinds.add(new EntityKind(type));
		}
	}
	
	public Optional<? extends EntityKindMatcher> getMatcher(String name) {
		if(!init) {
			init();
			init = true;
		}
		
		if(name.equals("*")) {
			return Optional.of(WILDCARD);
		}
		
		Optional<String> formattedName = format(name);
		if(!formattedName.isPresent()) {
			return Optional.empty();
		}
		
		return getByName(formattedName.get());
	}
	
	private Optional<String> format(String name) {
		Matcher matcher = ENTITY_NAME_PATTERN.matcher(name);
		if(!matcher.matches()) {
			return Optional.empty();
		}
		
		boolean hasModPrefix = matcher.group(MOD_PREFIX) != null;
		return Optional.of(hasModPrefix ? name : "minecraft:" + name);
	}
	
	private Optional<EntityKind> getByName(String name) {
		return allKinds.stream()
				.filter(e -> e.getType().getId().equals(name))
				.findFirst();
	}
}
