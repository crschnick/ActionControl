package org.monospark.actioncontrol.matcher.object;

import java.util.Optional;

import org.monospark.actioncontrol.matcher.KindRegistry;

public abstract class ObjectKindRegistry<K extends ObjectKind> extends KindRegistry<K> {

    public final Optional<K> getKind(String baseName, int variant) {
        tryInit();
        return get(baseName, variant);
    }

    protected abstract Optional<K> get(String baseName, int variant);
}
