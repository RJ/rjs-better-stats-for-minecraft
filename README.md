# rjs-better-stats

Fixes and additions to vanilla stat tracking. Each fix is independently toggleable via `config/rjs_better_stats.properties`.

### Client/server installation (important)

This mod registers **custom stats** (under `minecraft:custom`). Because custom stat IDs are **synced to clients during login**, players must use a **Fabric client with this mod installed** to join a server running it. Vanilla clients will disconnect with an “unknown registry entries” error.

### Fixes

| Config key                     | Description                                                                                                                   | Stats affected                                                  |
| ------------------------------ | ----------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------- |
| `shears_used`                  | Increment vanilla “shears used” stat when shears successfully shear an entity (vanilla misses this for right-click shearing). | `minecraft:used` → `minecraft:shears`                           |
| `flint_and_steel_used`         | Increment vanilla “flint and steel used” stat when flint and steel successfully triggers an entity interaction.               | `minecraft:used` → `minecraft:flint_and_steel`                  |
| `emeralds_spent_trading`       | Track how many emerald items you spend in successful trades (villagers + wandering traders).                                  | `minecraft:custom` → `rjs-better-stats:emeralds_spent_trading`  |
| `emeralds_earned_trading`      | Track how many emerald items you earn in successful trades (villagers + wandering traders).                                   | `minecraft:custom` → `rjs-better-stats:emeralds_earned_trading` |
| `creeper_ignition_counts_kill` | Fix MC-147347: lighting a creeper with flint and steel should count as killing a creeper in stats.                            | `minecraft:killed` → `minecraft:creeper`                        |

### Notes on custom stats

- Custom stat identifiers are registered in `com.metabrew.betterstats.stats.BetterStatsStats`.
- They live under vanilla’s `minecraft:custom` stat type (same as `minecraft:traded_with_villager`, etc.).

