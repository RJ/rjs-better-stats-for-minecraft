package com.metabrew.betterstats.fix;

import java.util.Set;

public final class EmeraldsSpentTrading implements Fix {
	@Override
	public String key() {
		return "emeralds_spent_trading";
	}

	@Override
	public boolean defaultEnabled() {
		return true;
	}

	@Override
	public String description() {
		return "Track how many emerald items you spend in successful villager trades (custom stat, server-side).";
	}

	@Override
	public Set<String> mixinClassNames() {
		return Set.of("com.metabrew.betterstats.mixin.fix.EmeraldsSpentTradingMixin");
	}
}

