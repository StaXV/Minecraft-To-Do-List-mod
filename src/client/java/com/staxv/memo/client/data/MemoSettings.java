package com.staxv.memo.client.data;

/**
 * Settings for the pinned in-game memo overlay. Serialized as JSON.
 */
public class MemoSettings {
	public boolean pinnedEnabled = false;
	public boolean pinnedOnlyUndone = true;
	public int pinnedMaxEntries = 5;
	public int pinnedWidth = 180;
	/** Position of the memo's top-left corner, as a percentage of screen width/height. */
	public int pinnedX = 3;
	public int pinnedY = 3;
	public int pinnedOpacity = 100;
}
