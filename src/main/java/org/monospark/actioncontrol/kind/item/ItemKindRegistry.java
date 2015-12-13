package org.monospark.actioncontrol.kind.item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.monospark.actioncontrol.kind.ObjectKindRegistry;
import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

public final class ItemKindRegistry extends ObjectKindRegistry<ItemKind, ItemKindMatcher> {

	private static final Map<ItemType, int[]> VANILLA_ITEM_VARIANTS = getVanillaItemVariants();
	
	private static Map<ItemType, int[]> getVanillaItemVariants() {
		Map<ItemType, int[]> variants = new HashMap<ItemType, int[]>();
		variants.put(ItemTypes.COAL, new int[] {1});
		variants.put(ItemTypes.GOLDEN_APPLE, new int[] {1});
		variants.put(ItemTypes.FISH, new int[] {1, 2, 3});
		variants.put(ItemTypes.COOKED_FISH, new int[] {0, 1, 2, 3});
		variants.put(ItemTypes.DYE, new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15});
		variants.put(ItemTypes.SPAWN_EGG, new int[] {50, 51, 52, 54, 55, 56, 57, 58, 59, 60, 61, 62,
				65, 66, 67, 68, 69, 90, 91, 92, 93, 94, 95, 96, 98, 100, 101, 120});
		variants.put(ItemTypes.SKULL, new int[] {1, 2, 3, 4});
		
		return variants;
	}
	
	private final Set<String> vanillaNames = new HashSet<String>();
	
	private final Set<ItemKind> allKinds = new HashSet<ItemKind>();

	ItemKindRegistry() {}
	
	protected void init() {
		for(ItemType type : Sponge.getRegistry().getAllOf(CatalogTypes.ITEM_TYPE)) {
			if(type.getBlock().isPresent()) {
				continue;
			}
			
			if(VANILLA_ITEM_VARIANTS.containsKey(type)) {
				vanillaNames.add(type.getName());
				allKinds.add(new ItemKind(type, 0));
				for(int variantNumber : VANILLA_ITEM_VARIANTS.get(type)) {
					allKinds.add(new ItemKind(type, variantNumber));
				}
			} else {
				allKinds.add(new ItemKind(type, 0));
			}
		}
	}

	@Override
	protected Optional<ItemKind> getKind(String baseName, int variant) {
		if(isVanillaItem(baseName)) {
			return getItemKind(baseName, variant);
		} else {
			Optional<ItemKind> kind = getItemKind(baseName, variant);
			if(kind.isPresent()) {
				return kind;
			}
			
			Optional<ItemType> type = getItemTypeByName(baseName);
			if(!type.isPresent()) {
				return Optional.empty();
			}
			ItemKind newKind = new ItemKind(type.get(), variant);
			allKinds.add(newKind);
			return Optional.of(newKind);
		}
	}
	
	private boolean isVanillaItem(String baseName) {
		for(String vanillaName : vanillaNames) {
			if(vanillaName.equals(baseName)) {
				return true;
			}
		}	
		return false;
	}
	
	private Optional<ItemKind> getItemKind(String baseName, int variant) {
		for(ItemKind kind : allKinds) {
			if(kind.getBaseName().equals(baseName) && kind.getVariant() == variant) {
				return Optional.of(kind);
			}
		}
		return Optional.empty();
	}
	
	private Optional<ItemType> getItemTypeByName(String name) {
		for(ItemType type : Sponge.getRegistry().getAllOf(CatalogTypes.ITEM_TYPE)) {
			if(type.getName().equals(name)) {
				return Optional.of(type);
			}
		}
		return Optional.empty();
	}

	@Override
	protected ItemKindMatcher createMatcher(Set<ItemKind> kinds) {
		return new ItemKindMatcher(kinds);
	}
}
