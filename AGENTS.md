# Instructions for Agents

This is a java minecraft mod, using fabric. latest version of minecraft (1.21.11 at the time of writing).

It fixes and collects various minecraft statistics that aren't counted by vanilla minecraft.

## How to add new stat fixes

This mod is organized as a set of **independent fixes**, each toggled by a **config key** in `config/rjs_better_stats.properties`.

## How fixes work
- **Config file**: `config/rjs_better_stats.properties`
  - Each fix has a boolean entry like `some_fix=true` or `some_fix=false`.
  - On startup, missing keys are appended with a short comment describing the fix.
- **Fix registry**: `com.metabrew.betterstats.fix.Fixes`
  - The list returned by `Fixes.all()` is the source of truth for:
    - which config keys exist
    - what gets documented/added to the config
    - which fix classes can be enabled
- **Conditional mixins**:
  - `src/main/resources/rjs-better-stats.mixins.json` lists the mixin classes that *could* apply.
  - `com.metabrew.betterstats.mixin.BetterStatsMixinPlugin` decides whether a given mixin actually applies based on the config key.

## Adding a new fix (checklist)
### 1) Pick the config key + class name
- Use a **snake_case** config key (example: `shears_used`).
- Create a Java class named in **PascalCase** (example: `ShearsUsed`).
- Keep names **specific** so fixes don’t collide or accidentally overlap behavior.

### 2) Create the fix class
Create `src/main/java/com/metabrew/betterstats/fix/<ClassName>.java` implementing `Fix`.

Minimum you must provide:
- `key()` → the config key
- `defaultEnabled()` → default when config doesn’t exist yet
- `description()` → written to the config file above the key
- `mixinClassNames()` → fully-qualified mixin class names (only if you use mixins)

### 3) Register the fix in `Fixes`
Add your new fix instance to the list in:
- `src/main/java/com/metabrew/betterstats/fix/Fixes.java`

If it’s not in `Fixes.all()`, it won’t get a config key generated/appended.

### 4) Add the mixin (if needed)
Create your mixin under:
- `src/main/java/com/metabrew/betterstats/mixin/fix/<ClassName>Mixin.java`

Guardrails:
- Keep injections **narrow** and only trigger when the action truly occurred.
- Avoid side effects. If you increment a stat, do it **server-side only**.
- Prefer injecting on `"RETURN"` and checking `ActionResult.isAccepted()` (or equivalent) so you don’t count failed interactions.

### 5) Add the mixin to the mixin config JSON
Add the mixin’s *short name* (relative to the `"package"` in the JSON) to:
- `src/main/resources/rjs-better-stats.mixins.json`

Example entry:
- `"fix.MyNewFixMixin"`

### 6) Ensure the mixin is tied to the fix key
The conditional plugin only knows which fix a mixin belongs to if the mixin’s **fully-qualified name** is returned from your fix’s `mixinClassNames()`.

If you forget this, the mixin may always apply (or not be toggleable as intended).

### 7) Build
Run:

```bash
./gradlew build
```

### 8) Update docs
- Update `README.md` so the **Fixes** table stays accurate (config key, description, and any related stat IDs).

### 9) Update language strings (if you add custom stats)
- If you add a new custom stat under `minecraft:custom`, add a translation entry so it shows up nicely in the Statistics UI:
  - File: `src/main/resources/assets/rjs-better-stats/lang/en_us.json`
  - Key format: `stat.<namespace>.<path>` (example: `stat.rjs-better-stats.xp_collected`)

## Config usage
To enable/disable a fix, edit:
- `config/rjs_better_stats.properties`

Example:

```properties
# Increment vanilla 'shears used' stat when shears successfully shear an entity (vanilla misses this for right-click shearing).
shears_used=true
```

