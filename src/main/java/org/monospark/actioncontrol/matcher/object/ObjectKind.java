package org.monospark.actioncontrol.matcher.object;

public abstract class ObjectKind {

    private String name;

    private int variant;

    protected ObjectKind(String name, int variant) {
        this.name = name;
        this.variant = variant;
    }

    public final String getBaseName() {
        return name;
    }

    public final String getName() {
        return name + (variant != 0 ? ":" + variant : "");
    }

    public final int getVariant() {
        return variant;
    }
}
