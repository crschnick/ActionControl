package org.monospark.actioncontrol.category;

import java.util.Optional;

public abstract class CategoryCompatibility {

    public abstract Optional<String> getMessage();

    private static final class Compatible extends CategoryCompatibility {

        private Compatible() {}

        @Override
        public Optional<String> getMessage() {
            return Optional.empty();
        }
    }

    private static final class RedundantMatchers extends CategoryCompatibility {

        @Override
        public Optional<String> getMessage() {
            return null;
        }
    }

    private static final class OverridingMatchers extends CategoryCompatibility {

        @Override
        public Optional<String> getMessage() {
            return null;
        }
    }
}
