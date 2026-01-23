package com.metabrew.betterstats.fix;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Fixes {
    private Fixes() {
    }

    private static final List<Fix> ALL = List.of(
            new ShearsUsed(),
            new FlintAndSteelUsed(),
            new XpEarned(),
            new EmeraldsSpentTrading(),
            new EmeraldsEarnedTrading(),
            new CreeperIgnitionCountsKill());

    private static final Map<String, Fix> BY_MIXIN_CLASS_NAME = ALL.stream()
            .flatMap(fix -> fix.mixinClassNames().stream().map(mixin -> Map.entry(mixin, fix)))
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

    public static List<Fix> all() {
        return ALL;
    }

    public static Fix forMixinClassName(String mixinClassName) {
        return BY_MIXIN_CLASS_NAME.get(mixinClassName);
    }
}
