package com.metabrew.betterstats.mixin;

import com.metabrew.betterstats.Rjsbetterstats;
import com.metabrew.betterstats.config.BetterStatsConfig;
import com.metabrew.betterstats.fix.Fix;
import com.metabrew.betterstats.fix.Fixes;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class BetterStatsMixinPlugin implements IMixinConfigPlugin {
    private BetterStatsConfig config;

    @Override
    public void onLoad(String mixinPackage) {
        this.config = BetterStatsConfig.loadOrCreate();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        Fix fix = Fixes.forMixinClassName(mixinClassName);
        if (fix == null) {
            return true;
        }

        boolean enabled = config != null ? config.isEnabled(fix) : fix.defaultEnabled();
        if (!enabled) {
            Rjsbetterstats.LOGGER.info("Disabled fix via config: {} ({})", fix.key(), mixinClassName);
        }
        return enabled;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
