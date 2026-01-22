package com.metabrew.betterstats.mixin.fix;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class FlintAndSteelUsedMixin {
    @Unique
    private int rjsbetterstats$flintAndSteelDamageBeforeMainHand = -1;

    @Unique
    private int rjsbetterstats$flintAndSteelDamageBeforeOffHand = -1;

    @Inject(method = "interact", at = @At("HEAD"))
    private void rjsbetterstats$rememberFlintAndSteelDamageBeforeInteract(Entity entity, Hand hand,
            CallbackInfoReturnable<ActionResult> cir) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (self.getEntityWorld().isClient()) {
            return;
        }

        ItemStack stack = self.getStackInHand(hand);
        int before = stack.isOf(Items.FLINT_AND_STEEL) ? stack.getDamage() : -1;
        if (hand == Hand.MAIN_HAND) {
            this.rjsbetterstats$flintAndSteelDamageBeforeMainHand = before;
        } else {
            this.rjsbetterstats$flintAndSteelDamageBeforeOffHand = before;
        }
    }

    /**
     * Vanilla doesn't consistently increment flint and steel's {@link Stats#USED} when it is used
     * via entity interaction (e.g. igniting a creeper).
     *
     * We increment on the server whenever the interaction is accepted and it looks like flint and
     * steel actually did something (item damage changed) or the target is a known flint+steel
     * ignition interaction.
     */
    @Inject(method = "interact", at = @At("RETURN"))
    private void rjsbetterstats$incrementFlintAndSteelUsedOnSuccessfulEntityUse(Entity entity, Hand hand,
            CallbackInfoReturnable<ActionResult> cir) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (self.getEntityWorld().isClient()) {
            return;
        }

        int beforeDamage;
        if (hand == Hand.MAIN_HAND) {
            beforeDamage = this.rjsbetterstats$flintAndSteelDamageBeforeMainHand;
            this.rjsbetterstats$flintAndSteelDamageBeforeMainHand = -1;
        } else {
            beforeDamage = this.rjsbetterstats$flintAndSteelDamageBeforeOffHand;
            this.rjsbetterstats$flintAndSteelDamageBeforeOffHand = -1;
        }

        if (beforeDamage < 0) {
            return;
        }

        ActionResult result = cir.getReturnValue();
        if (result == null || !result.isAccepted()) {
            return;
        }

        ItemStack afterStack = self.getStackInHand(hand);
        boolean stackBroke = afterStack.isEmpty();
        boolean damageChanged = !stackBroke && afterStack.isOf(Items.FLINT_AND_STEEL) && afterStack.getDamage() != beforeDamage;
        boolean knownIgnitionEntity = entity instanceof CreeperEntity || entity instanceof TntMinecartEntity;

        if (stackBroke || damageChanged || knownIgnitionEntity) {
            self.incrementStat(Stats.USED.getOrCreateStat(Items.FLINT_AND_STEEL));
        }
    }
}

