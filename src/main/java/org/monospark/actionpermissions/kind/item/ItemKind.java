package org.monospark.actionpermissions.kind.item;

import org.monospark.actionpermissions.kind.ObjectKind;
import org.spongepowered.api.item.ItemType;

public final class ItemKind extends ObjectKind {

	private static final ItemKindRegistry registry = new ItemKindRegistry();

	public static ItemKindRegistry getRegistry() {
		return registry;
	}
	
	private ItemType type;
	
	ItemKind(ItemType type, int variant) {
		super(variant);
		this.type = type;
	}

	@Override
	protected String getBaseName() {
		return type.getName();
	}
	
	public ItemType getItemType() {
		return type;
	}
}
