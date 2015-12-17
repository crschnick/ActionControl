package org.monospark.actioncontrol.handler.blockplace;

import java.lang.reflect.Type;

import org.monospark.actioncontrol.handler.ActionMatcher;
import org.monospark.actioncontrol.kind.KindType;
import org.monospark.actioncontrol.kind.matcher.KindMatcher;
import org.spongepowered.api.block.BlockState;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public final class BlockPlaceMatcher extends ActionMatcher {

	private KindMatcher blockMatcher;

	BlockPlaceMatcher(KindMatcher blockMatcher) {
		this.blockMatcher = blockMatcher;
	}
	
	public boolean matches(BlockState state) {
		return blockMatcher.matchesBlockState(state);
	}
	
	public static final class Deserializer implements JsonDeserializer<BlockPlaceMatcher> {

		@Override
		public BlockPlaceMatcher deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject object = json.getAsJsonObject();
			KindMatcher blockMatcher = KindType.ITEM.getDeserializer().deserialize(object.get("blockIds"));
			return new BlockPlaceMatcher(blockMatcher);
		}
	}

	KindMatcher getBlockMatcher() {
		return blockMatcher;
	}
}
