package com.jg.memo.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import com.jg.memo.client.data.MemoEntry;
import com.jg.memo.client.data.MemoStore;

/**
 * Add / edit a single memo, using vanilla widgets.
 */
public class MemoEditScreen extends Screen {
	private static final int COLUMN_WIDTH = 320;

	private final MemoEntry editing;

	private EditBox titleBox;
	private MultiLineEditBox contentArea;
	private int contentBottom;

	public MemoEditScreen(MemoEntry editing) {
		super(Component.translatable(editing == null ? "memo.edit.title" : "memo.edit.title_edit"));
		this.editing = editing;
	}

	@Override
	protected void init() {
		int innerX = (width - COLUMN_WIDTH) / 2;

		titleBox = new EditBox(font, innerX, 44, COLUMN_WIDTH, 20,
				Component.translatable("memo.edit.title"));
		titleBox.setMaxLength(80);
		titleBox.setHint(Component.translatable("memo.edit.title_placeholder"));
		if (editing != null) {
			titleBox.setValue(editing.title);
		}

		int contentTop = 70;
		contentBottom = height - 74;
		contentArea = MultiLineEditBox.builder()
				.setX(innerX)
				.setY(contentTop)
				.setPlaceholder(Component.translatable("memo.edit.content_placeholder"))
				.setTextColor(0xFFE0E0E0)
				.setTextShadow(true)
				.setCursorColor(0xFFE0E0E0)
				.setShowBackground(true)
				.setShowDecorations(true)
				.build(font, COLUMN_WIDTH, Math.max(80, contentBottom - contentTop),
						Component.translatable("memo.edit.content_placeholder"));
		contentArea.setCharacterLimit(20000);
		if (editing != null) {
			contentArea.setValue(editing.content);
		}

		Button save = Button.builder(Component.translatable("memo.edit.save"),
				btn -> save()).bounds(innerX + COLUMN_WIDTH - 100, contentBottom + 28, 100, 20).build();
		Button cancel = Button.builder(Component.translatable("memo.edit.cancel"),
				btn -> onClose()).bounds(innerX + COLUMN_WIDTH - 210, contentBottom + 28, 100, 20).build();

		addRenderableWidget(titleBox);
		addRenderableWidget(contentArea);
		addRenderableWidget(save);
		addRenderableWidget(cancel);

		setInitialFocus(titleBox);
	}

	private void save() {
		String title = titleBox.getValue().trim();
		String content = contentArea.getValue();
		if (title.isEmpty() && content.isBlank()) {
			onClose();
			return;
		}
		if (editing == null) {
			MemoStore.get().add(title, content);
		} else {
			MemoStore.get().update(editing.id, title, content);
		}
		onClose();
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractBackground(graphics, mouseX, mouseY, delta);

		MemoRender.textCentered(graphics, font, title, width / 2, 14, 0xFFFFFFFF);
		MemoRender.textCentered(graphics, font, Component.translatable("memo.edit.title_label"),
				width / 2, 30, 0xFFA0A0B0);

	}

	@Override
	public void onClose() {
		minecraft.gui.setScreen(new MemoScreen());
	}
}
