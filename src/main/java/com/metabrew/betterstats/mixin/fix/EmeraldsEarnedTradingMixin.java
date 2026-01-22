package com.metabrew.betterstats.mixin.fix;

import com.metabrew.betterstats.stats.BetterStatsStats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.TradeOutputSlot;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TradeOutputSlot.class)
public class EmeraldsEarnedTradingMixin {
    /**
     * Inject after {@code merchant.trade(tradeOffer)} so we only count successful
     * trades.
     * We compute "earned emeralds" from the offer's sell item.
     */
    @Inject(method = "onTakeItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/Merchant;trade(Lnet/minecraft/village/TradeOffer;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void rjsbetterstats$incrementEmeraldsEarnedTrading(PlayerEntity player, ItemStack stack, CallbackInfo ci,
            TradeOffer tradeOffer, ItemStack firstBuyStack, ItemStack secondBuyStack) {
        if (player.getEntityWorld().isClient()) {
            return;
        }

        ItemStack sell = tradeOffer.getSellItem();
        if (!sell.isOf(Items.EMERALD)) {
            return;
        }

        int earned = sell.getCount();
        if (earned > 0) {
            player.increaseStat(BetterStatsStats.EMERALDS_EARNED_TRADING, earned);
        }
    }
}
