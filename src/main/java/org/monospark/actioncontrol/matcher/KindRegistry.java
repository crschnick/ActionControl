package org.monospark.actioncontrol.matcher;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class KindRegistry<K> {

    private boolean init;

    protected Set<K> allKinds;

    protected final void tryInit() {
        if (!init) {
            init();
            init = true;
        }
    }

    private void init() {
        allKinds = new HashSet<K>();
        initRegistry();
    }

    protected abstract void initRegistry();

    public final Set<K> getAllKinds() {
        tryInit();

        return Collections.unmodifiableSet(allKinds);
    }
}
