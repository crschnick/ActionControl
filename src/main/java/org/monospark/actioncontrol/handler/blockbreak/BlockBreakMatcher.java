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

	private Set<ToolSettings> toolSettings;
	
	BlockBreakMatcher(Set<ToolSettings> toolSettings) {
		this.toolSettings = toolSettings;
	}

	boolean matches(Optional<ItemStack> tool, BlockState block) {
		for(ToolSettings s : toolSettings) {
			if(s.getTools().matchesItemStack(tool.orElseGet(() -> null)) && s.getBlocks().matchesBlockState(block)) {
				return true;
			}
		}
		return false;
	}

	Set<ToolSettings> getToolSettings() {
		return toolSettings;
	}

	
	static final class Deserializer implements JsonDeserializer<BlockBreakMatcher> {

		@Override
		public BlockBreakMatcher deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			ToolSettings[] toolSettings = context.deserialize(json, ToolSettings[].class);
			return new BlockBreakMatcher(Sets.newHashSet(toolSettings));
		}
	}
	
	static final class ToolSettings {
		
		private KindMatcher tools;
		
		private KindMatcher blocks;

		private ToolSettings(KindMatcher tools, KindMatcher blocks) {
			this.tools = tools;
			this.blocks = blocks;
		}

		private KindMatcher getTools() {
			return tools;
		}

		private KindMatcher getBlocks() {
			return blocks;
		}
		
		
		static final class Deserializer implements JsonDeserializer<ToolSettings> {
			
			@Override
			public ToolSettings deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException {
				JsonObject object = json.getAsJsonObject();
				KindMatcher toolMatcher = KindType.ITEM.getDeserializer().deserialize(object.get("toolIds"));
				KindMatcher blockMatcher = KindType.BLOCK.getDeserializer().deserialize(object.get("blockIds"));
				return new ToolSettings(toolMatcher, blockMatcher);
			}
		}
	}
}
