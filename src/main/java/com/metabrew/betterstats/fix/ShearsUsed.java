package com.metabrew.betterstats.fix;

import java.util.Set;

public final class ShearsUsed implements Fix {
    @Override
    public String key() {
        return "shears_used";
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @Override
    public String description() {
        return "Increment vanilla 'shears used' stat when shears successfully shear an entity (vanilla misses this for right-click shearing).";
    }

    @Override
    public Set<String> mixinClassNames() {
        return Set.of("com.metabrew.betterstats.mixin.fix.ShearsUsedMixin");
    }
}
