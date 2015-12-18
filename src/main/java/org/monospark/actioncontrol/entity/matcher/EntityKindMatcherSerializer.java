package org.monospark.actioncontrol.entity.matcher;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.monospark.actioncontrol.entity.EntityKind;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class EntityKindMatcherSerializer {

	public static EntityKindMatcher deserialize(JsonElement json) {
		if(json.isJsonArray()) {
			JsonArray array = json.getAsJsonArray();
			Set<EntityKindMatcher> matchers = new HashSet<EntityKindMatcher>();
			for(int i = 0; i < array.size(); i++) {
				String name = array.get(i).getAsString();
				Optional<? extends EntityKindMatcher> matcher = EntityKind.getRegistry().getMatcher(name);
				matchers.add(matcher.get());
			}
			return new EntityKindMatcherAmount(matchers);
		} else {
			String name = json.getAsString();
			return EntityKind.getRegistry().getMatcher(name).get();
		}
	}
}
