package com.metabrew.betterstats.mixin.fix;

import com.metabrew.betterstats.access.CreeperIgnitionCountsKillAccess;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * MC-147347: Lighting a creeper with flint and steel doesn't count as "mob
 * killed" in stats.
 *
 * We remember who ignited the creeper (server-side) and, if the creeper later
 * dies to its own
 * explosion (i.e. no external attacker), we credit that player with the creeper
 * kill stat.
 */
@Mixin(CreeperEntity.class)
public class CreeperIgnitionCountsKillMixin implements CreeperIgnitionCountsKillAccess {
    @Unique
    private UUID rjsbetterstats$ignitedByPlayerUuid;

    @Override
    public UUID rjsbetterstats$getIgnitedByPlayerUuid() {
        return this.rjsbetterstats$ignitedByPlayerUuid;
    }

    @Override
    public void rjsbetterstats$setIgnitedByPlayerUuid(UUID uuid) {
        this.rjsbetterstats$ignitedByPlayerUuid = uuid;
    }

    @Inject(method = "interactMob", at = @At("RETURN"))
    private void rjsbetterstats$rememberIgniter(PlayerEntity player, Hand hand,
            CallbackInfoReturnable<ActionResult> cir) {
        CreeperEntity self = (CreeperEntity) (Object) this;
        if (self.getEntityWorld().isClient()) {
            return;
        }

        ItemStack stack = player.getStackInHand(hand);
        if (!stack.isOf(Items.FLINT_AND_STEEL)) {
            return;
        }

        ActionResult result = cir.getReturnValue();
        if (result != null && result.isAccepted()) {
            this.rjsbetterstats$ignitedByPlayerUuid = player.getUuid();
        }
    }

    /**
     * MC-147347: When the creeper actually explodes (from having been ignited via
     * flint+steel),
     * credit the igniter with a creeper kill stat.
     *
     * This avoids relying on DamageSource details, which can differ for explosions.
     */
    @Inject(method = "explode", at = @At("TAIL"))
    private void rjsbetterstats$creditIgniterOnExplode(CallbackInfo ci) {
        CreeperEntity self = (CreeperEntity) (Object) this;
        if (self.getEntityWorld().isClient()) {
            return;
        }

        UUID igniterUuid = this.rjsbetterstats$ignitedByPlayerUuid;
        if (igniterUuid == null) {
            return;
        }

        // Clear first to avoid any chance of double-credit.
        this.rjsbetterstats$ignitedByPlayerUuid = null;

        ServerWorld world = (ServerWorld) self.getEntityWorld();
        ServerPlayerEntity igniter = world.getServer().getPlayerManager().getPlayer(igniterUuid);
        if (igniter != null) {
            igniter.increaseStat(Stats.KILLED.getOrCreateStat(self.getType()), 1);
        }
    }
}
