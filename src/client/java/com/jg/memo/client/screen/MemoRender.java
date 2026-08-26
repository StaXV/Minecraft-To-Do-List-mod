package com.jg.memo.client.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

/**
 * Small drawing helpers shared by the memo screens. All textures are vanilla
 * GUI sprites so the interface matches Minecraft's own look.
 */
public final class MemoRender {
	private static Identifier vanilla(String path) {
		return Identifier.withDefaultNamespace(path);
	}

	public static final Identifier BUTTON = vanilla("widget/button");
	public static final Identifier BUTTON_HIGHLIGHTED = vanilla("widget/button_highlighted");
	public static final Identifier BUTTON_DISABLED = vanilla("widget/button_disabled");
	public static final Identifier CHECKBOX = vanilla("widget/checkbox");
	public static final Identifier CHECKBOX_SELECTED = vanilla("widget/checkbox_selected");
	/** Rounded translucent toast that scales cleanly (nine-slice), so the mini window keeps nice corners at any height. */
	public static final Identifier TOAST = vanilla("friends/toast_background");
	public static final Identifier SCROLLER = vanilla("widget/scroller");
	public static final Identifier SCROLLER_BACKGROUND = vanilla("widget/scroller_background");
	public static final Identifier BOOK = Identifier.withDefaultNamespace("textures/gui/book.png");

	private MemoRender() {
	}

	/** Draws a vanilla nine-sliced sprite stretched to the given rectangle. */
	public static void sprite(GuiGraphicsExtractor graphics, Identifier sprite, int x, int y, int width, int height) {
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height);
	}

	/** Draws the vanilla open-book background (192x192, like the book screen). */
	public static void book(GuiGraphicsExtractor graphics, int left, int top) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, BOOK, left, top, 0, 0, 192, 192, 256, 256);
	}

	/**
	 * Draws a crisp small checkbox. Used in the pinned HUD where the vanilla
	 * sprite would be scaled down too far and the checkmark would become
	 * incomplete/blurry.
	 */
	public static void miniCheckbox(GuiGraphicsExtractor graphics, int x, int y, int size, boolean checked) {
		miniCheckbox(graphics, x, y, size, checked, 255);
	}

	/** Same as above, but with an explicit alpha (0-255) for the whole checkbox. */
	public static void miniCheckbox(GuiGraphicsExtractor graphics, int x, int y, int size, boolean checked, int alpha) {
		int border = withAlpha(0xFF9A9AB0, alpha);
		int fill = withAlpha(0xFF20202C, alpha);
		int check = withAlpha(0xFFFFFFFF, alpha);
		graphics.fill(x, y, x + size, y + 1, border);
		graphics.fill(x, y + size - 1, x + size, y + size, border);
		graphics.fill(x, y, x + 1, y + size, border);
		graphics.fill(x + size - 1, y, x + size, y + size, border);
		graphics.fill(x + 1, y + 1, x + size - 1, y + size - 1, fill);
		if (checked) {
			// Checkmark as two thick segments: down-right, then up-right.
			checkSegment(graphics, x + size / 5, y + size * 3 / 5, x + size * 2 / 5, y + size * 4 / 5, check);
			checkSegment(graphics, x + size * 2 / 5, y + size * 4 / 5, x + size * 4 / 5, y + size / 4, check);
		}
	}

	private static void checkSegment(GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, int color) {
		int dx = x1 - x0;
		int dy = y1 - y0;
		int steps = Math.max(Math.abs(dx), Math.abs(dy));
		for (int i = 0; i <= steps; i++) {
			int px = x0 + dx * i / steps;
			int py = y0 + dy * i / steps;
			graphics.fill(px, py, px + 2, py + 2, color);
		}
	}

	public static int withAlpha(int color, int alpha) {
		return (color & 0x00FFFFFF) | (alpha << 24);
	}

	public static void text(GuiGraphicsExtractor graphics, Font font, Component text, int x, int y, int color) {
		graphics.text(font, text, x, y, color, true);
	}

	public static void textPlain(GuiGraphicsExtractor graphics, Font font, String text, int x, int y, int color) {
		graphics.text(font, text, x, y, color, true);
	}

	public static void textCentered(GuiGraphicsExtractor graphics, Font font, Component text,
			int centerX, int y, int color) {
		graphics.text(font, text, centerX - font.width(text) / 2, y, color, true);
	}
}
