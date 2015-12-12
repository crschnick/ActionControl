package org.monospark.actionpermissions.kind;

public abstract class ObjectKind {

	private int variant;

	protected ObjectKind(int variant) {
		this.variant = variant;
	}

	public final String getName() {
		return getBaseName() + (variant != 0 ? variant : "");
	}
	
	protected abstract String getBaseName();

	public final int getVariant() {
		return variant;
	}
}
