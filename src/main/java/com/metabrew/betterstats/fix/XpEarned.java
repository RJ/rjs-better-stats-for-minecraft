package com.metabrew.betterstats.fix;

import java.util.Set;

/**
 * Vanilla doesn't provide a built-in stat for "experience gained" from picking up experience orbs.
 * This fix registers a custom stat and increments it when XP is granted to the player.
 */
public final class XpEarned implements Fix {
    @Override
    public String key() {
        return "xp_collected";
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @Override
    public String description() {
        return "Track how much XP you collect from XP orbs (custom stat; counts the orb's full XP value, even if mending consumes some).";
    }

    @Override
    public Set<String> mixinClassNames() {
        return Set.of("com.metabrew.betterstats.mixin.fix.XpEarnedMixin");
    }
}

