package org.monospark.actioncontrol.kind;

import java.util.Optional;

import org.monospark.actioncontrol.kind.matcher.KindMatcher;

public interface KindRegistry {

	Optional<? extends KindMatcher> getMatcher(String name);
}
