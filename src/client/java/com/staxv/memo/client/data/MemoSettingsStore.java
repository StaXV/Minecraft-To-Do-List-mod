package com.staxv.memo.client.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

import com.staxv.memo.MemoMod;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Loads and persists the memo overlay settings as JSON in the config directory.
 */
public final class MemoSettingsStore {
	private static MemoSettingsStore instance;

	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private final Path file;
	private final MemoSettings settings = new MemoSettings();

	private MemoSettingsStore() {
		file = FabricLoader.getInstance().getConfigDir().resolve("memo_settings.json");
		load();
	}

	public static MemoSettingsStore get() {
		if (instance == null) {
			instance = new MemoSettingsStore();
		}
		return instance;
	}

	public MemoSettings settings() {
		return settings;
	}

	private void load() {
		if (!Files.exists(file)) {
			return;
		}
		try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			MemoSettings loaded = gson.fromJson(reader, MemoSettings.class);
			if (loaded != null) {
				settings.pinnedEnabled = loaded.pinnedEnabled;
				settings.pinnedOnlyUndone = loaded.pinnedOnlyUndone;
				settings.pinnedMaxEntries = clamp(loaded.pinnedMaxEntries, 1, 5);
				settings.pinnedWidth = clamp(loaded.pinnedWidth, 100, 320);
				settings.pinnedX = clamp(loaded.pinnedX, 0, 100);
				settings.pinnedY = clamp(loaded.pinnedY, 0, 100);
				settings.pinnedOpacity = clamp(loaded.pinnedOpacity, 0, 100);
			}
		} catch (IOException | RuntimeException e) {
			MemoMod.LOGGER.warn("Failed to load memo settings, using defaults.", e);
		}
	}

	public void save() {
		try {
			Files.createDirectories(file.getParent());
			Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
			try (Writer writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
				gson.toJson(settings, writer);
			}
			Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			MemoMod.LOGGER.warn("Failed to save memo settings.", e);
		}
	}

	private static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}
}
