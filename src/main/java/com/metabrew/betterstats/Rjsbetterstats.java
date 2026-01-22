package com.metabrew.betterstats;

import com.metabrew.betterstats.config.BetterStatsConfig;
import com.metabrew.betterstats.fix.Fix;
import com.metabrew.betterstats.fix.Fixes;
import com.metabrew.betterstats.stats.BetterStatsStats;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rjsbetterstats implements ModInitializer {
	public static final String MOD_ID = "rjs-better-stats";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		BetterStatsStats.register();

		BetterStatsConfig config = BetterStatsConfig.loadOrCreate();
		config.ensureAllFixKeysPresent(Fixes.all());

		for (Fix fix : Fixes.all()) {
			if (!config.isEnabled(fix)) {
				continue;
			}
			try {
				fix.onInitialize();
				LOGGER.info("Enabled fix: {} ({})", fix.key(), fix.getClass().getSimpleName());
			} catch (Exception e) {
				LOGGER.error("Failed to initialize fix: {} ({})", fix.key(), fix.getClass().getSimpleName(), e);
			}
		}

		LOGGER.info("rjs-better-stats initialized");
	}
}