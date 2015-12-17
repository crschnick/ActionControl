package org.monospark.actioncontrol.handler.blockinteract;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;

import org.monospark.actioncontrol.handler.ActionMatcher;
import org.monospark.actioncontrol.kind.KindType;
import org.monospark.actioncontrol.kind.matcher.KindMatcher;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.item.inventory.ItemStack;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

final class BlockInteractMatcher extends ActionMatcher {

	private Set<MatcherData> data;

	BlockInteractMatcher(Set<MatcherData> data) {
		this.data = data;
	}

	boolean matches(Optional<ItemStack> item, BlockState block) {
		for(MatcherData matcherData : data) {
			if(matcherData.getItemMatcher().matchesItemStack(item.orElse(null)) &&
					matcherData.getBlockMatcher().matchesBlockState(block)) {
				return true;
			}
		}
		return false;
	}

	Set<MatcherData> getData() {
		return data;
	}


	static final class Deserializer implements JsonDeserializer<BlockInteractMatcher> {

		@Override
		public BlockInteractMatcher deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			MatcherData[] data = context.deserialize(json, MatcherData[].class);
			return new BlockInteractMatcher(Sets.newHashSet(data));
		}
	}
	
	
	static final class MatcherData {
		
		private KindMatcher items;
		
		private KindMatcher blocks;

		private MatcherData(KindMatcher items, KindMatcher blocks) {
			this.items = items;
			this.blocks = blocks;
		}

		private KindMatcher getItemMatcher() {
			return items;
		}

		private KindMatcher getBlockMatcher() {
			return blocks;
		}
		
		
		static final class Deserializer implements JsonDeserializer<MatcherData> {
			
			@Override
			public MatcherData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException {
				JsonObject object = json.getAsJsonObject();
				KindMatcher itemMatcher = KindType.ITEM.getDeserializer().deserialize(object.get("itemIds"));
				KindMatcher blockMatcher = KindType.BLOCK.getDeserializer().deserialize(object.get("blockIds"));
				return new MatcherData(itemMatcher, blockMatcher);
			}
		}
	}
}
