package com.metabrew.betterstats.fix;

import java.util.Set;

public interface Fix {
    /**
     * Config key used in rjs_better_stats.properties (e.g. {@code shears_used}).
     */
    String key();

    /**
     * Whether this fix should default to enabled when no config exists yet.
     */
    boolean defaultEnabled();

    /**
     * Human-readable description written into the generated config file.
     */
    String description();

    /**
     * Fully-qualified mixin class names that implement this fix.
     */
    default Set<String> mixinClassNames() {
        return Set.of();
    }

    /**
     * Optional runtime hook initialization (events, etc). Called only when the fix
     * is enabled.
     */
    default void onInitialize() {
    }
}
