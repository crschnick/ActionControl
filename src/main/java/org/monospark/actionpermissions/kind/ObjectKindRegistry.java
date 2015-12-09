package org.monospark.actionpermissions.kind;

import java.util.Optional;

import org.monospark.actionpermissions.kind.block.BlockKind;
import org.monospark.actionpermissions.kind.item.ItemKind;

public final class ObjectKindRegistry {

	private ObjectKindRegistry() {}
	
	public static Optional<? extends ObjectKind> getObjectKind(String name) {		
		Optional<BlockKind> blockKind = BlockKind.getRegistry().getBlockKind(name);
		if(blockKind.isPresent()) {
			return blockKind;
		}
		
		Optional<ItemKind> itemKind = ItemKind.getRegistry().getItemKind(name);
		if(itemKind.isPresent()) {
			return itemKind;
		}
		
		return Optional.empty();
	}
}
