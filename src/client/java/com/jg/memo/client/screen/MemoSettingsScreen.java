package com.jg.memo.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import com.jg.memo.client.data.MemoSettings;
import com.jg.memo.client.data.MemoSettingsStore;
import com.jg.memo.client.screen.widget.SettingsSlider;

/**
 * Settings screen, reachable from Mod Menu or from the memo screen itself.
 * Position is edited by opening the position screen, which shows the live game.
 */
public class MemoSettingsScreen extends Screen {
	private static final int COLUMN_WIDTH = 320;

	private final Screen parent;
	private final MemoSettings settings = MemoSettingsStore.get().settings();

	private int contentLabelY;
	private int appearanceLabelY;
	private int positionLabelY;
	private int footerY;

	public MemoSettingsScreen(Screen parent) {
		super(Component.translatable("memo.settings.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		int cx = (width - COLUMN_WIDTH) / 2;
		int topY = Math.max(16, (height - 280) / 2);

		appearanceLabelY = topY;
		SettingsSlider entries = new SettingsSlider(cx, topY + 14, COLUMN_WIDTH, 20, "memo.settings.entries",
				(settings.pinnedMaxEntries - 1) / 4.0,
				v -> 1 + Math.round(v * 4),
				v -> {
					settings.pinnedMaxEntries = (int) Math.round(1 + v * 4);
					MemoSettingsStore.get().save();
				});

		SettingsSlider width = new SettingsSlider(cx, topY + 38, COLUMN_WIDTH, 20, "memo.settings.width",
				(settings.pinnedWidth - 100) / 220.0,
				v -> 100 + Math.round(v * 220),
				v -> {
					settings.pinnedWidth = (int) Math.round(100 + v * 220);
					MemoSettingsStore.get().save();
				});

		SettingsSlider opacity = new SettingsSlider(cx, topY + 62, COLUMN_WIDTH, 20, "memo.settings.opacity",
				settings.pinnedOpacity / 100.0,
				v -> Math.round(v * 100),
				v -> {
					settings.pinnedOpacity = (int) Math.round(v * 100);
					MemoSettingsStore.get().save();
				});

		contentLabelY = topY + 86;
		Checkbox enabled = Checkbox.builder(Component.translatable("memo.settings.pinned"), font)
				.pos(cx, topY + 100)
				.selected(settings.pinnedEnabled)
				.onValueChange((checkbox, value) -> {
					settings.pinnedEnabled = value;
					MemoSettingsStore.get().save();
				})
				.build();

		Checkbox onlyUndone = Checkbox.builder(Component.translatable("memo.settings.only_undone"), font)
				.pos(cx, topY + 122)
				.selected(settings.pinnedOnlyUndone)
				.onValueChange((checkbox, value) -> {
					settings.pinnedOnlyUndone = value;
					MemoSettingsStore.get().save();
				})
				.build();

		positionLabelY = topY + 144;
		Button position = Button.builder(Component.translatable("memo.settings.position_button"), btn -> {
			minecraft.gui.setScreen(new MemoPositionScreen(this));
		}).bounds(cx, topY + 158, COLUMN_WIDTH, 20).build();

		footerY = topY + 186;
		Button reset = Button.builder(Component.translatable("memo.settings.reset"), btn -> {
			settings.pinnedX = 3;
			settings.pinnedY = 3;
			MemoSettingsStore.get().save();
		}).bounds(cx, footerY, 120, 20).build();
		Button done = Button.builder(Component.translatable("memo.settings.done"),
				btn -> onClose()).bounds(cx + COLUMN_WIDTH - 120, footerY, 120, 20).build();

		addRenderableWidget(enabled);
		addRenderableWidget(onlyUndone);
		addRenderableWidget(entries);
		addRenderableWidget(width);
		addRenderableWidget(opacity);
		addRenderableWidget(position);
		addRenderableWidget(reset);
		addRenderableWidget(done);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractBackground(graphics, mouseX, mouseY, delta);
		MemoRender.textCentered(graphics, font, title, width / 2, 16, 0xFFFFFFFF);
		MemoRender.textCentered(graphics, font, Component.translatable("memo.settings.section_content"),
				width / 2, contentLabelY, 0xFFA0A0B0);
		MemoRender.textCentered(graphics, font, Component.translatable("memo.settings.section_appearance"),
				width / 2, appearanceLabelY, 0xFFA0A0B0);
		MemoRender.textCentered(graphics, font, Component.translatable("memo.settings.position_label"),
				width / 2, positionLabelY, 0xFFA0A0B0);
	}

	@Override
	public void onClose() {
		minecraft.gui.setScreen(parent);
	}
}
