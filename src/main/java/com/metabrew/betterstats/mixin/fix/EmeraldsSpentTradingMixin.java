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
public class EmeraldsSpentTradingMixin {
	/**
	 * Inject after {@code merchant.trade(tradeOffer)} so we only count successful trades.
	 * We compute "spent emeralds" based on the offer's displayed buy items, which includes price adjustments.
	 */
	@Inject(
		method = "onTakeItem",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/village/Merchant;trade(Lnet/minecraft/village/TradeOffer;)V", shift = At.Shift.AFTER),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void rjsbetterstats$incrementEmeraldsSpentTrading(PlayerEntity player, ItemStack stack, CallbackInfo ci, TradeOffer tradeOffer, ItemStack firstBuyStack, ItemStack secondBuyStack) {
		if (player.getEntityWorld().isClient()) {
			return;
		}

		int spent = 0;
		ItemStack firstBuy = tradeOffer.getDisplayedFirstBuyItem();
		if (firstBuy.isOf(Items.EMERALD)) {
			spent += firstBuy.getCount();
		}

		ItemStack secondBuy = tradeOffer.getDisplayedSecondBuyItem();
		if (secondBuy.isOf(Items.EMERALD)) {
			spent += secondBuy.getCount();
		}

		if (spent > 0) {
			player.increaseStat(BetterStatsStats.EMERALDS_SPENT_TRADING, spent);
		}
	}
}

