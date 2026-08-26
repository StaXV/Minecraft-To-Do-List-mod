package com.staxv.memo.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import com.staxv.memo.client.MemoModClient;
import com.staxv.memo.client.data.MemoEntry;
import com.staxv.memo.client.data.MemoStore;

import java.util.ArrayList;
import java.util.List;

/**
 * The to-do list, presented like the vanilla book: ink text on parchment,
 * paginated five entries per page, with a star button and (for done entries)
 * a delete button on each row.
 */
public class MemoScreen extends Screen {
	private static final int BOOK_SIZE = 192;
	private static final int ITEMS_PER_PAGE = 5;
	private static final int ROW_HEIGHT = 22;
	private static final int ICON = 11;
	private static final long CONFIRM_TIMEOUT = 2500L;

	private static final int INK = 0xFF403218;
	private static final int INK_DONE = 0xFFA09070;

	private final List<MemoEntry> memos = new ArrayList<>();

	private int bookLeft;
	private int bookTop;
	private int listX;
	private int listY;
	private int listW;
	private int listBottom;

	private int page;
	private int pageCount;

	private boolean clearArmed;
	private long clearArmedAt;

	private Button newButton;
	private Button clearButton;
	private Button settingsButton;
	private PageButton forwardButton;
	private PageButton backButton;

	public MemoScreen() {
		super(Component.translatable("memo.screen.title"));
	}

	@Override
	protected void init() {
		memos.clear();
		memos.addAll(MemoStore.get().memos());

		bookLeft = (width - BOOK_SIZE) / 2;
		bookTop = 2;
		listX = bookLeft + 40;
		listW = 116;
		listY = bookTop + 24;
		listBottom = bookTop + 150;

		pageCount = Math.max(1, (memos.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE);
		page = Math.max(0, Math.min(page, pageCount - 1));

		int actionY = bookTop + BOOK_SIZE + 8;
		settingsButton = Button.builder(Component.translatable("memo.screen.settings"),
				btn -> openSettings()).bounds(width / 2 - 124, actionY, 80, 20).build();
		clearButton = Button.builder(Component.translatable("memo.screen.clear"),
				btn -> handleClear()).bounds(width / 2 + 44, actionY, 80, 20).build();
		newButton = Button.builder(Component.literal("＋"),
				btn -> openEdit(null)).bounds(width / 2 - 24, actionY, 48, 20).build();

		forwardButton = new PageButton(bookLeft + 116, bookTop + 157, true, btn -> turnPage(1), true);
		backButton = new PageButton(bookLeft + 43, bookTop + 157, false, btn -> turnPage(-1), true);

		addRenderableWidget(newButton);
		addRenderableWidget(settingsButton);
		addRenderableWidget(clearButton);
		addRenderableWidget(forwardButton);
		addRenderableWidget(backButton);

		updatePageButtons();
	}

	private void openEdit(MemoEntry memo) {
		minecraft.gui.setScreen(new MemoEditScreen(memo));
	}

	private void openSettings() {
		minecraft.gui.setScreen(new MemoSettingsScreen(this));
	}

	private void turnPage(int delta) {
		int next = page + delta;
		if (next < 0 || next >= pageCount) {
			return;
		}
		page = next;
		updatePageButtons();
	}

	private void updatePageButtons() {
		forwardButton.visible = page < pageCount - 1;
		backButton.visible = page > 0;
	}

	private void handleClear() {
		long now = System.currentTimeMillis();
		if (clearArmed && now - clearArmedAt < CONFIRM_TIMEOUT) {
			MemoStore.get().clear();
			clearArmed = false;
			page = 0;
			init();
		} else {
			clearArmed = true;
			clearArmedAt = now;
			clearButton.setMessage(Component.translatable("memo.screen.clear_confirm"));
		}
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractBackground(graphics, mouseX, mouseY, delta);
		MemoRender.book(graphics, bookLeft, bookTop);
		MemoRender.textCentered(graphics, font, Component.translatable("memo.screen.title"),
				width / 2, 14, INK);

		if (clearArmed && System.currentTimeMillis() - clearArmedAt >= CONFIRM_TIMEOUT) {
			clearArmed = false;
			clearButton.setMessage(Component.translatable("memo.screen.clear"));
		}

		if (memos.isEmpty()) {
			renderEmpty(graphics);
		} else {
			renderList(graphics, mouseX, mouseY);
		}
	}

	private void renderEmpty(GuiGraphicsExtractor graphics) {
		Component empty = Component.translatable("memo.screen.empty");
		List<FormattedCharSequence> lines = font.split(empty, Math.max(40, listW - 20));
		int centerY = (listY + listBottom) / 2 - (lines.size() * font.lineHeight) / 2;
		for (int i = 0; i < lines.size(); i++) {
			graphics.centeredText(font, lines.get(i), width / 2, centerY + i * font.lineHeight, INK);
		}
	}

	private void renderList(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		int start = page * ITEMS_PER_PAGE;
		int end = Math.min(start + ITEMS_PER_PAGE, memos.size());
		graphics.enableScissor(listX, listY, listX + listW, listBottom);
		for (int i = start; i < end; i++) {
			renderRow(graphics, memos.get(i), listY + (i - start) * ROW_HEIGHT);
		}
		graphics.disableScissor();
	}

	private void renderRow(GuiGraphicsExtractor graphics, MemoEntry memo, int rowY) {
		MemoRender.sprite(graphics, memo.done ? MemoRender.CHECKBOX_SELECTED : MemoRender.CHECKBOX,
				listX, rowY + 5, ICON, ICON);

		String text = memo.title != null && !memo.title.isBlank()
				? memo.title
				: (memo.content == null || memo.content.isBlank() ? "（无标题）" : firstLine(memo.content));
		int color = memo.done ? INK_DONE : INK;

		int starX = listX + listW - ICON;
		int delX = starX - ICON - 1;
		int maxWidth = memo.done ? listW - 40 : listW - 28;
		if (font.width(text) > maxWidth && maxWidth > 10) {
			text = font.plainSubstrByWidth(text, maxWidth);
		}
		Component line = Component.literal(text).withStyle(style -> style.withColor(color).withStrikethrough(memo.done));
		graphics.text(font, line, listX + 15, rowY + 6, color, false);

		// Delete button (cross) only for done entries, to the left of the star.
		if (memo.done) {
			MemoRender.sprite(graphics, MemoRender.CROSS, delX, rowY + 5, ICON, ICON);
		}

		// Star button, always on the far right.
		MemoRender.sprite(graphics, memo.starred ? MemoRender.STAR_FILLED : MemoRender.STAR,
				starX, rowY + 5, ICON, ICON);
	}

	private String firstLine(String text) {
		int nl = text.indexOf('\n');
		return nl < 0 ? text : text.substring(0, nl);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
		double mouseX = event.x();
		double mouseY = event.y();
		if (mouseX >= listX && mouseX < listX + listW && mouseY >= listY && mouseY < listBottom) {
			int localIndex = (int) ((mouseY - listY) / ROW_HEIGHT);
			int index = page * ITEMS_PER_PAGE + localIndex;
			if (localIndex >= 0 && localIndex < ITEMS_PER_PAGE && index < memos.size()) {
				handleRowClick(memos.get(index), event.buttonInfo().button(), mouseX);
				return true;
			}
		}
		return super.mouseClicked(event, bl);
	}

	private void handleRowClick(MemoEntry memo, int button, double mouseX) {
		int starX = listX + listW - ICON;
		int delX = starX - ICON - 1;

		if (button == 1) {
			confirmDelete(memo);
			return;
		}

		if (button == 0 && mouseX >= starX && mouseX < starX + ICON) {
			toggleStar(memo);
			return;
		}

		if (button == 0 && memo.done && mouseX >= delX && mouseX < delX + ICON) {
			confirmDelete(memo);
			return;
		}

		if (button == 0 && mouseX >= listX && mouseX < listX + ICON) {
			MemoStore.get().toggle(memo.id);
			init();
			return;
		}

		if (button == 0) {
			openEdit(memo);
		}
	}

	private void toggleStar(MemoEntry memo) {
		if (!memo.starred && MemoStore.get().starredCount() >= MemoStore.MAX_STARRED) {
			minecraft.gui.toastManager().addToast(new SystemToast(
					SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
					Component.translatable("memo.star.limit.title"),
					Component.translatable("memo.star.limit")));
			return;
		}
		MemoStore.get().toggleStar(memo.id);
		init();
	}

	private void confirmDelete(MemoEntry memo) {
		String name = memo.title != null && !memo.title.isBlank()
				? memo.title
				: (memo.content == null || memo.content.isBlank() ? "" : firstLine(memo.content));
		Component message = Component.translatable("memo.card.delete_confirm_message", name);
		minecraft.gui.setScreen(new ConfirmScreen(ok -> {
			if (ok) {
				MemoStore.get().remove(memo.id);
			}
			minecraft.gui.setScreen(new MemoScreen());
		}, Component.translatable("memo.card.delete_confirm_title"), message,
				Component.translatable("memo.card.delete"), Component.translatable("memo.edit.cancel")));
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (MemoModClient.openKey.matches(event)) {
			onClose();
			return true;
		}
		return super.keyPressed(event);
	}
}
