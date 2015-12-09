package org.monospark.actionpermissions.kind.item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

public final class ItemKindRegistry {

	private static final Pattern ITEM_KIND_PATTERN = Pattern.compile("(\\w+?:\\w+?)(?::(\\d+))?");
	
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
	
	private final Set<ItemKind> allKinds = new HashSet<ItemKind>();

	ItemKindRegistry() {}
	
	public void initRegistry() {
		for(ItemType type : Sponge.getRegistry().getAllOf(CatalogTypes.ITEM_TYPE)) {
			if(type.getBlock().isPresent()) {
				continue;
			}
			
			Set<ItemKindVariant> variants = new HashSet<ItemKindVariant>();
			variants.add(new ItemKindVariant(type, 0));
			if(VANILLA_ITEM_VARIANTS.containsKey(type)) {
				for(int variantNumber : VANILLA_ITEM_VARIANTS.get(type)) {
					variants.add(new ItemKindVariant(type, variantNumber));
				}
			}
			
			ItemKindVariantAmount variantAmount = new ItemKindVariantAmount(type, variants);
			allKinds.add(variantAmount);
		}
	}
	
	public Optional<ItemKind> getItemKind(String name) {
		Matcher matcher = ITEM_KIND_PATTERN.matcher(name);
		if (!matcher.matches()) {
			return Optional.empty();
		}
		
		String itemTypeName = matcher.group(1);
		ItemKind matchedKind = null;
		for (ItemKind kind : allKinds) {
			if (kind.getItemType().getName().equals(itemTypeName)) {
				matchedKind = kind;
				break;
			}
		}
		if (matchedKind == null) {
			return Optional.empty();
		}
		
		if (matcher.groupCount() == 3) {
			int variantNumber = Integer.parseInt(matcher.group(2));
			for(ItemKindVariant variant : ((ItemKindVariantAmount) matchedKind).getVariants()) {
				if(variant.getVariantNumber() == variantNumber) {
					return Optional.of(variant);
				}
			}
			
			ItemKindVariant kindVariant = new ItemKindVariant(matchedKind.getItemType(), variantNumber);
			((ItemKindVariantAmount) matchedKind).addVariant(kindVariant);
			return Optional.of(kindVariant);
		} else {
			return Optional.of(matchedKind);
		}
	}
}
