package com.staxv.memo.client.data;

import java.util.UUID;

/**
 * A single memo note. Stored as JSON and edited from the memo screens.
 */
public class MemoEntry {
	public String id = UUID.randomUUID().toString();
	public String title = "";
	public String content = "";
	public boolean done = false;
	public boolean starred = false;
	public long createdAt = System.currentTimeMillis();
	public long updatedAt = System.currentTimeMillis();

	public MemoEntry() {
	}

	public MemoEntry(String title, String content) {
		this.title = title == null ? "" : title;
		this.content = content == null ? "" : content;
	}

	public void touch() {
		updatedAt = System.currentTimeMillis();
	}

	public String preview() {
		String text = content.isBlank() ? title : content;
		return text.isBlank() ? "" : text.replace('\n', ' ');
	}
}
