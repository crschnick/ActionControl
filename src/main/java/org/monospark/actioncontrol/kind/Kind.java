package org.monospark.actioncontrol.kind;

import org.monospark.actioncontrol.kind.matcher.KindMatcher;

public abstract class Kind implements KindMatcher {

	private String name;
	
	private int variant;
	
	private KindType<?,?> type;

	protected Kind(String name, int variant, KindType<?,?> type) {
		this.name = name;
		this.variant = variant;
		this.type = type;
	}
	
	public final String getBaseName() {
		return name;
	}

	public final String getName() {
		return name + ":" + (variant != 0 ? variant : "");
	}
	
	public final int getVariant() {
		return variant;
	}

	public final KindType<?,?> getType() {
		return type;
	}
}
