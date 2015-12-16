package org.monospark.actioncontrol.kind.matcher;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public abstract class KindMatcherDeserializer {

	public KindMatcher deserialize(JsonElement json) throws JsonParseException {
		if(json.isJsonArray()) {
			JsonArray array = json.getAsJsonArray();
			KindMatcherAmount.Builder builder = KindMatcherAmount.builder();
			for(int i = 0; i < array.size(); i++) {
				KindMatcher matcher = deserializeMatcher(array.get(i).getAsString());
				builder.addKindMatcher(matcher);
			}
			return builder.build();
		} else {
			String string = json.getAsString();
			return deserializeMatcher(string);
		}
	}

	protected abstract KindMatcher deserializeMatcher(String name);
}
