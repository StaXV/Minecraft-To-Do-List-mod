package com.jg.memo.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import com.jg.memo.client.data.MemoSettings;
import com.jg.memo.client.data.MemoSettingsStore;
import com.jg.memo.client.hud.PinnedMemoHud;

/**
 * Position editor: the live game is shown behind the screen so you can see the
 * mini to-do overlay exactly where it will appear, then drag it into place.
 */
public class MemoPositionScreen extends Screen {
	private final Screen parent;
	private final MemoSettings settings = MemoSettingsStore.get().settings();
	private final int originalX;
	private final int originalY;

	private boolean dragging;
	private double dragOffsetX;
	private double dragOffsetY;

	public MemoPositionScreen(Screen parent) {
		super(Component.translatable("memo.pos.title"));
		this.parent = parent;
		this.originalX = settings.pinnedX;
		this.originalY = settings.pinnedY;
	}

	@Override
	protected void init() {
		int midY = height - 92;
		Button confirm = Button.builder(Component.translatable("memo.pos.confirm"),
				btn -> closeKeep()).bounds(width / 2 - 50, midY, 100, 20).build();
		Button cancel = Button.builder(Component.translatable("memo.pos.cancel"),
				btn -> closeRevert()).bounds(width / 2 - 50, midY + 26, 100, 20).build();
		addRenderableWidget(confirm);
		addRenderableWidget(cancel);
	}

	private void closeKeep() {
		MemoSettingsStore.get().save();
		minecraft.gui.setScreen(parent);
	}

	private void closeRevert() {
		settings.pinnedX = originalX;
		settings.pinnedY = originalY;
		MemoSettingsStore.get().save();
		minecraft.gui.setScreen(parent);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		// Lightly dim, but keep the live world visible behind the screen.
		graphics.fill(0, 0, width, height, 0x30000000);

		// The mini to-do overlay at its real position.
		PinnedMemoHud.renderMini(graphics, Minecraft.getInstance(), settings);

		// Hint.
		MemoRender.textCentered(graphics, font, Component.translatable("memo.pos.hint"),
				width / 2, height - 124, 0xFFFFFFFF);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
		if (event.buttonInfo().button() == 0) {
			PinnedMemoHud.Rect rect = PinnedMemoHud.rect(settings,
					minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
			double mx = event.x();
			double my = event.y();
			if (mx >= rect.x() && mx < rect.x() + rect.w() && my >= rect.y() && my < rect.y() + rect.h()) {
				dragging = true;
				dragOffsetX = mx - rect.x();
				dragOffsetY = my - rect.y();
				return true;
			}
		}
		return super.mouseClicked(event, bl);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double offsetX, double offsetY) {
		if (dragging) {
			int gw = minecraft.getWindow().getGuiScaledWidth();
			int gh = minecraft.getWindow().getGuiScaledHeight();
			PinnedMemoHud.Rect rect = PinnedMemoHud.rect(settings, gw, gh);
			double mx = event.x() - dragOffsetX;
			double my = event.y() - dragOffsetY;
			int boxX = PinnedMemoHud.clamp((int) mx, 4, gw - rect.w() - 4);
			int boxY = PinnedMemoHud.clamp((int) my, 4, gh - rect.h() - 4);
			settings.pinnedX = boxX * 100 / gw;
			settings.pinnedY = boxY * 100 / gh;
			MemoSettingsStore.get().save();
			return true;
		}
		return super.mouseDragged(event, offsetX, offsetY);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		dragging = false;
		return super.mouseReleased(event);
	}

	@Override
	public void onClose() {
		closeRevert();
	}
}
