package org.monospark.actioncontrol.matcher.entity;

import java.util.Optional;

import org.monospark.actioncontrol.matcher.KindRegistry;
import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;

public final class EntityKindRegistry extends KindRegistry<EntityKind> {

    @Override
    protected void initRegistry() {
        for (EntityType type : Sponge.getRegistry().getAllOf(CatalogTypes.ENTITY_TYPE)) {
            allKinds.add(new EntityKind(type));
        }
    }

    public Optional<EntityKind> getKind(String name) {
        return getAllKinds().stream()
                .filter(e -> e.getType().getId().equals(name))
                .findFirst();
    }
}
