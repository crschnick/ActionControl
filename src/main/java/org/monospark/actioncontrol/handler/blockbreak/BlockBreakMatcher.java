package org.monospark.actioncontrol.handler.blockbreak;

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

public final class BlockBreakMatcher extends ActionMatcher {

	private Set<MatcherData> data;
	
	BlockBreakMatcher(Set<MatcherData> data) {
		this.data = data;
	}

	boolean matches(Optional<ItemStack> tool, BlockState block) {
		for(MatcherData s : data) {
			if(s.getTools().matchesItemStack(tool.orElse(null)) && s.getBlocks().matchesBlockState(block)) {
				return true;
			}
		}
		return false;
	}

	Set<MatcherData> getToolSettings() {
		return data;
	}

	
	static final class Deserializer implements JsonDeserializer<BlockBreakMatcher> {

		@Override
		public BlockBreakMatcher deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			MatcherData[] data = context.deserialize(json, MatcherData[].class);
			return new BlockBreakMatcher(Sets.newHashSet(data));
		}
	}
	
	
	static final class MatcherData {
		
		private KindMatcher tools;
		
		private KindMatcher blocks;

		private MatcherData(KindMatcher tools, KindMatcher blocks) {
			this.tools = tools;
			this.blocks = blocks;
		}

		private KindMatcher getTools() {
			return tools;
		}

		private KindMatcher getBlocks() {
			return blocks;
		}
		
		
		static final class Deserializer implements JsonDeserializer<MatcherData> {
			
			@Override
			public MatcherData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException {
				JsonObject object = json.getAsJsonObject();
				KindMatcher toolMatcher = KindType.ITEM.getDeserializer().deserialize(object.get("toolIds"));
				KindMatcher blockMatcher = KindType.BLOCK.getDeserializer().deserialize(object.get("blockIds"));
				return new MatcherData(toolMatcher, blockMatcher);
			}
		}
	}
}
