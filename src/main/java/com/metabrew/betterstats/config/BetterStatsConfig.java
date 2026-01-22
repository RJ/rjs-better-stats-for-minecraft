package com.metabrew.betterstats.config;

import com.metabrew.betterstats.Rjsbetterstats;
import com.metabrew.betterstats.fix.Fix;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class BetterStatsConfig {
	private static final String FILE_NAME = "rjs_better_stats.properties";

	private final Path path;
	private final Map<String, Boolean> enabledByKey;

	private BetterStatsConfig(Path path, Map<String, Boolean> enabledByKey) {
		this.path = path;
		this.enabledByKey = enabledByKey;
	}

	public static BetterStatsConfig loadOrCreate() {
		Path configDir;
		try {
			configDir = FabricLoader.getInstance().getConfigDir();
		} catch (Throwable t) {
			// Very early bootstrap fallback (should be rare)
			configDir = Path.of("config");
		}
		Path path = configDir.resolve(FILE_NAME);

		Map<String, Boolean> enabled = new HashMap<>();
		if (!Files.exists(path)) {
			return new BetterStatsConfig(path, enabled);
		}

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#") || line.startsWith("!")) {
					continue;
				}
				int idx = line.indexOf('=');
				if (idx < 0) {
					continue;
				}
				String key = line.substring(0, idx).trim();
				String value = line.substring(idx + 1).trim();
				if (key.isEmpty()) {
					continue;
				}
				if ("true".equalsIgnoreCase(value)) {
					enabled.put(key, true);
				} else if ("false".equalsIgnoreCase(value)) {
					enabled.put(key, false);
				}
			}
		} catch (IOException e) {
			Rjsbetterstats.LOGGER.error("Failed reading config: {}", path, e);
		}

		return new BetterStatsConfig(path, enabled);
	}

	public Path path() {
		return path;
	}

	public boolean isEnabled(Fix fix) {
		Boolean configured = enabledByKey.get(fix.key());
		return configured != null ? configured : fix.defaultEnabled();
	}

	/**
	 * Ensures the config file exists and contains entries for all known fixes.
	 * Existing keys are never modified; missing keys are appended with docs.
	 */
	public void ensureAllFixKeysPresent(List<Fix> fixes) {
		try {
			Files.createDirectories(path.getParent());
		} catch (IOException e) {
			Rjsbetterstats.LOGGER.error("Failed creating config directory for {}", path, e);
			return;
		}

		if (!Files.exists(path)) {
			writeFreshFile(fixes);
			return;
		}

		Set<String> existingKeys = new HashSet<>();
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#") || line.startsWith("!")) {
					continue;
				}
				int idx = line.indexOf('=');
				if (idx < 0) {
					continue;
				}
				String key = line.substring(0, idx).trim();
				if (!key.isEmpty()) {
					existingKeys.add(key);
				}
			}
		} catch (IOException e) {
			Rjsbetterstats.LOGGER.error("Failed scanning existing config: {}", path, e);
			return;
		}

		StringBuilder toAppend = new StringBuilder();
		for (Fix fix : fixes) {
			if (existingKeys.contains(fix.key())) {
				continue;
			}
			appendFixBlock(toAppend, fix);
		}
		if (toAppend.isEmpty()) {
			return;
		}

		try (BufferedWriter writer = Files.newBufferedWriter(
			path,
			StandardCharsets.UTF_8,
			StandardOpenOption.WRITE,
			StandardOpenOption.APPEND
		)) {
			writer.newLine();
			writer.write("# ---- added by rjs-better-stats (new fixes) ----");
			writer.newLine();
			writer.write(toAppend.toString());
		} catch (IOException e) {
			Rjsbetterstats.LOGGER.error("Failed appending missing keys to config: {}", path, e);
		}
	}

	private void writeFreshFile(List<Fix> fixes) {
		StringBuilder out = new StringBuilder();
		out.append("# rjs-better-stats configuration").append('\n');
		out.append("#").append('\n');
		out.append("# Toggle fixes with key=true/false.").append('\n');
		out.append("#").append('\n');

		for (Fix fix : fixes) {
			appendFixBlock(out, fix);
		}

		try (BufferedWriter writer = Files.newBufferedWriter(
			path,
			StandardCharsets.UTF_8,
			StandardOpenOption.CREATE_NEW,
			StandardOpenOption.WRITE
		)) {
			writer.write(out.toString());
		} catch (IOException e) {
			Rjsbetterstats.LOGGER.error("Failed writing config: {}", path, e);
		}
	}

	private static void appendFixBlock(StringBuilder out, Fix fix) {
		out.append("# ").append(fix.description()).append('\n');
		out.append(fix.key()).append('=').append(fix.defaultEnabled()).append('\n');
		out.append('\n');
	}
}

