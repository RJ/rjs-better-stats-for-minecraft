package com.metabrew.betterstats.fix;

import java.util.Set;

/**
 * MC-147347: Lighting a creeper with a flint and steel doesn't count as a "mob
 * killed" stat for creepers.
 */
public final class CreeperIgnitionCountsKill implements Fix {
    @Override
    public String key() {
        return "creeper_ignition_counts_kill";
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @Override
    public String description() {
        return "Fix MC-147347: lighting a creeper with flint and steel should count as killing a creeper in stats.";
    }

    @Override
    public Set<String> mixinClassNames() {
        return Set.of("com.metabrew.betterstats.mixin.fix.CreeperIgnitionCountsKillMixin");
    }
}
