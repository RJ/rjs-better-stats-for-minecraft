package com.metabrew.betterstats.mixin.fix;

import com.metabrew.betterstats.stats.BetterStatsStats;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ExperienceOrbEntity.class)
public class XpEarnedMixin {
    /**
     * Track total XP contained in the experience orb when it is picked up, regardless of whether
     * some XP is consumed by mending/repairs.
     */
    @Redirect(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;getValue()I"))
    private int rjsbetterstats$trackXpEarned(ExperienceOrbEntity orb, PlayerEntity player) {
        int value = orb.getValue();
        if (!orb.getEntityWorld().isClient() && value > 0) {
            player.increaseStat(BetterStatsStats.XP_COLLECTED, value);
        }
        return value;
    }
}

