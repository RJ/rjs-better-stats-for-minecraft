package com.metabrew.betterstats.access;

import java.util.UUID;

/**
 * Internal access bridge so non-creeper mixins (e.g. a LivingEntity death hook) can read/write per-creeper state.
 *
 * IMPORTANT: This must NOT live under {@code com.metabrew.betterstats.mixin.*} because mixin packages
 * are not allowed to be referenced directly at runtime.
 */
public interface CreeperIgnitionCountsKillAccess {
	UUID rjsbetterstats$getIgnitedByPlayerUuid();

	void rjsbetterstats$setIgnitedByPlayerUuid(UUID uuid);
}

