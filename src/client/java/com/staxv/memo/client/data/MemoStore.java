package com.staxv.memo.client.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.fabricmc.loader.api.FabricLoader;

import com.staxv.memo.MemoMod;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Loads and persists the memo list as JSON in the Minecraft config directory.
 * All mutations are saved immediately so nothing is lost when the game closes.
 */
public final class MemoStore {
	private static MemoStore instance;

	private static final Type LIST_TYPE = new TypeToken<ArrayList<MemoEntry>>() {
	}.getType();

	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private final Path file;
	private final List<MemoEntry> memos = new ArrayList<>();

	private MemoStore() {
		file = FabricLoader.getInstance().getConfigDir().resolve("memo.json");
		load();
	}

	public static MemoStore get() {
		if (instance == null) {
			instance = new MemoStore();
		}
		return instance;
	}

	private void load() {
		if (!Files.exists(file)) {
			return;
		}
		try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			List<MemoEntry> loaded = gson.fromJson(reader, LIST_TYPE);
			if (loaded != null) {
				memos.clear();
				for (MemoEntry entry : loaded) {
					if (entry == null || entry.id == null || entry.id.isBlank()) {
						continue;
					}
					if (entry.title == null) {
						entry.title = "";
					}
					if (entry.content == null) {
						entry.content = "";
					}
					memos.add(entry);
				}
			}
		} catch (IOException | RuntimeException e) {
			MemoMod.LOGGER.warn("Failed to load memo data, starting with an empty memo.", e);
		}
	}

	/** Returns memos sorted by: not-done first, then most recently updated first. */
	public List<MemoEntry> memos() {
		List<MemoEntry> sorted = new ArrayList<>(memos);
		sorted.sort(Comparator.comparingLong(e -> e.createdAt));
		return sorted;
	}

	public Optional<MemoEntry> byId(String id) {
		return memos.stream().filter(e -> e.id.equals(id)).findFirst();
	}

	public MemoEntry add(String title, String content) {
		MemoEntry entry = new MemoEntry(title, content);
		memos.add(entry);
		save();
		return entry;
	}

	public boolean update(String id, String title, String content) {
		Optional<MemoEntry> found = byId(id);
		if (found.isEmpty()) {
			return false;
		}
		MemoEntry entry = found.get();
		entry.title = title == null ? "" : title;
		entry.content = content == null ? "" : content;
		entry.touch();
		save();
		return true;
	}

	public boolean toggle(String id) {
		Optional<MemoEntry> found = byId(id);
		if (found.isEmpty()) {
			return false;
		}
		MemoEntry entry = found.get();
		entry.done = !entry.done;
		entry.touch();
		save();
		return true;
	}

	public boolean remove(String id) {
		boolean removed = memos.removeIf(e -> e.id.equals(id));
		if (removed) {
			save();
		}
		return removed;
	}

	public void clear() {
		memos.clear();
		save();
	}

	public void save() {
		try {
			Files.createDirectories(file.getParent());
			Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
			try (Writer writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
				gson.toJson(memos, writer);
			}
			Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			MemoMod.LOGGER.warn("Failed to save memo data.", e);
		}
	}
}
