package com.metabrew.betterstats.mixin.fix;

import net.minecraft.entity.Entity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class ShearsUsedMixin {
    /**
     * Vanilla doesn't consistently increment shears' {@link Stats#USED} when shears
     * are used on entities.
     * We increment the stat on the server whenever the interaction is accepted
     * (i.e. shearing happened).
     */
    @Inject(method = "interact", at = @At("RETURN"))
    private void rjsbetterstats$incrementShearsUsedOnSuccessfulShear(Entity entity, Hand hand,
            CallbackInfoReturnable<ActionResult> cir) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (self.getEntityWorld().isClient()) {
            return;
        }

        if (!(entity instanceof Shearable)) {
            return;
        }

        ItemStack stack = self.getStackInHand(hand);
        if (!stack.isOf(Items.SHEARS)) {
            return;
        }

        ActionResult result = cir.getReturnValue();
        if (result != null && result.isAccepted()) {
            self.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
        }
    }
}
