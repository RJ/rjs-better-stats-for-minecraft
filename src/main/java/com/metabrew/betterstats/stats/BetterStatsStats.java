package com.metabrew.betterstats.stats;

import com.metabrew.betterstats.Rjsbetterstats;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

public final class BetterStatsStats {
	private BetterStatsStats() {
	}

	/**
	 * Custom stat ID that tracks how much experience a player gains from picking up experience
	 * orbs (the orb's full value; includes XP that may be consumed by mending/repairs).
	 * Stored under {@link Stats#CUSTOM}.
	 */
	public static final Identifier XP_COLLECTED = Identifier.of(Rjsbetterstats.MOD_ID, "xp_collected");

	/**
	 * Custom stat ID that tracks how many emerald items a player spends in villager trades.
	 * Stored under {@link Stats#CUSTOM}.
	 */
	public static final Identifier EMERALDS_SPENT_TRADING = Identifier.of(Rjsbetterstats.MOD_ID, "emeralds_spent_trading");

	/**
	 * Custom stat ID that tracks how many emerald items a player earns in villager trades.
	 * Stored under {@link Stats#CUSTOM}.
	 */
	public static final Identifier EMERALDS_EARNED_TRADING = Identifier.of(Rjsbetterstats.MOD_ID, "emeralds_earned_trading");

	public static void register() {
		registerCustomStat(XP_COLLECTED);
		registerCustomStat(EMERALDS_SPENT_TRADING);
		registerCustomStat(EMERALDS_EARNED_TRADING);
	}

	private static void registerCustomStat(Identifier id) {
		Registry.register(Registries.CUSTOM_STAT, id, id);
		Stats.CUSTOM.getOrCreateStat(id, StatFormatter.DEFAULT);
	}
}

