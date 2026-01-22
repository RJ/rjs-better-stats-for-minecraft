package com.metabrew.betterstats.fix;

import java.util.Set;

/**
 * Vanilla doesn't consistently increment flint and steel's {@code minecraft:used} stat when
 * flint and steel is used via entity interaction (e.g. igniting a creeper).
 */
public final class FlintAndSteelUsed implements Fix {
    @Override
    public String key() {
        return "flint_and_steel_used";
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @Override
    public String description() {
        return "Increment vanilla 'flint and steel used' stat when flint and steel successfully triggers an entity interaction (vanilla misses some entity use paths).";
    }

    @Override
    public Set<String> mixinClassNames() {
        return Set.of("com.metabrew.betterstats.mixin.fix.FlintAndSteelUsedMixin");
    }
}

