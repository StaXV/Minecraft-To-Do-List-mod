package com.staxv.memo.client.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import com.staxv.memo.client.data.MemoEntry;
import com.staxv.memo.client.data.MemoSettings;
import com.staxv.memo.client.data.MemoSettingsStore;
import com.staxv.memo.client.data.MemoStore;
import com.staxv.memo.client.screen.MemoRender;

import net.minecraft.client.renderer.RenderPipelines;

import java.util.ArrayList;
import java.util.List;

/**
 * A small to-do overlay pinned to the HUD, styled like the vanilla
 * advancement toast. Its position is set in the position screen where the
 * live game is shown behind it.
 */
public final class PinnedMemoHud {
	/** Dimensions chosen so the toast texture is stretched as little as possible. */
	private static final int HEADER_HEIGHT = 12;
	private static final int ROW_HEIGHT = 10;

	private PinnedMemoHud() {
	}

	/** Renders the overlay from the HUD (only when enabled and no screen is open). */
	public static void render(GuiGraphicsExtractor graphics, DeltaTracker tracker) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.gui.screen() != null) {
			return;
		}
		MemoSettings settings = MemoSettingsStore.get().settings();
		if (!settings.pinnedEnabled) {
			return;
		}
		if (settings.pinnedOpacity <= 0) {
			return;
		}
		renderMini(graphics, mc, settings, settings.pinnedOpacity / 100f);
	}

	/** Draws the mini to-do box at full opacity. Used by the position screen so it stays draggable. */
	public static void renderMini(GuiGraphicsExtractor graphics, Minecraft mc, MemoSettings settings) {
		renderMini(graphics, mc, settings, 1f);
	}

	private static void renderMini(GuiGraphicsExtractor graphics, Minecraft mc, MemoSettings settings, float alpha) {
		int alpha255 = (int) (alpha * 255);
		Rect rect = rect(settings, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, MemoRender.TOAST, rect.x, rect.y, rect.w, rect.h, alpha);
		graphics.text(mc.font, Component.translatable("memo.pinned.title"), rect.x + 6, rect.y + 2,
				MemoRender.withAlpha(0xFFE8E8F0, alpha255), true);

		int rowY = rect.y + HEADER_HEIGHT;
		for (MemoEntry memo : rows(settings)) {
			MemoRender.miniCheckbox(graphics, rect.x + 6, rowY + 1, 9, memo.done, alpha255);
			String line = memo.title != null && !memo.title.isBlank()
					? memo.title
					: (memo.content == null || memo.content.isBlank() ? "" : firstLine(memo.content));
			String cut = mc.font.plainSubstrByWidth(line, Math.max(10, rect.w - 20));
			graphics.text(mc.font, cut, rect.x + 19, rowY + 1,
					MemoRender.withAlpha(memo.done ? 0xFF9A9AA8 : 0xFFF0F0F0, alpha255), true);
			rowY += ROW_HEIGHT;
		}
	}

	/** The on-screen rectangle the overlay occupies for the given settings and screen size. */
	public static Rect rect(MemoSettings settings, int gw, int gh) {
		int rows = rows(settings).size();
		int w = Math.max(140, settings.pinnedWidth);
		int h = HEADER_HEIGHT + rows * ROW_HEIGHT + 6;
		int x = clamp(gw * settings.pinnedX / 100, 4, gw - w - 4);
		int y = clamp(gh * settings.pinnedY / 100, 4, gh - h - 4);
		return new Rect(x, y, w, h);
	}

	private static List<MemoEntry> rows(MemoSettings settings) {
		List<MemoEntry> rows = new ArrayList<>();
		for (MemoEntry memo : MemoStore.get().memos()) {
			if (settings.pinnedOnlyUndone && memo.done) {
				continue;
			}
			rows.add(memo);
			if (rows.size() >= settings.pinnedMaxEntries) {
				break;
			}
		}
		return rows;
	}

	private static String firstLine(String text) {
		int nl = text.indexOf('\n');
		return nl < 0 ? text : text.substring(0, nl);
	}

	public static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	public record Rect(int x, int y, int w, int h) {
	}
}
