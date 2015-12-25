package org.monospark.actioncontrol.matcher.object;

import java.util.Optional;

public abstract class ObjectKindRegistry<K extends ObjectKind> {

    protected abstract Optional<K> getKind(String baseName, int variant);
}
