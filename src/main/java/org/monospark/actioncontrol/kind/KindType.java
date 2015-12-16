package org.monospark.actioncontrol.kind;

import java.util.Optional;

import org.monospark.actioncontrol.kind.matcher.KindMatcher;
import org.monospark.actioncontrol.kind.matcher.KindMatcherDeserializer;
import org.monospark.actioncontrol.kind.object.ObjectKindRegistry;
import org.monospark.actioncontrol.kind.object.block.BlockKind;
import org.monospark.actioncontrol.kind.object.block.BlockKindRegistry;
import org.monospark.actioncontrol.kind.object.item.ItemKind;
import org.monospark.actioncontrol.kind.object.item.ItemKindRegistry;

public final class KindType<K extends Kind, R extends KindRegistry>{

	public static final KindType<ItemKind, ItemKindRegistry> ITEM =
			new KindType<ItemKind, ItemKindRegistry>(new ItemKindRegistry());
	
	public static final KindType<BlockKind, BlockKindRegistry> BLOCK =
			new KindType<BlockKind, BlockKindRegistry>(new BlockKindRegistry());
	
	public static final KindType<Kind, ObjectKindRegistry> OBJECT =
			new KindType<Kind, ObjectKindRegistry>(new ObjectKindRegistry());
	
	private R registry;
	
	private KindMatcherDeserializer deserializer;
	
	private KindType(R registry) {
		this.registry = registry;
		this.deserializer = new KindMatcherDeserializer() {
			
			@Override
			protected KindMatcher deserializeMatcher(String name) {
				Optional<? extends KindMatcher> matcher = registry.getMatcher(name);
				return matcher.isPresent() ? matcher.get() : null;
			}
		};
	}
	
	public R getRegistry() {
		return this.registry;
	}

	public KindMatcherDeserializer getDeserializer() {
		return deserializer;
	}
}
