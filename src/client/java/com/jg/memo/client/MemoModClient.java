package com.jg.memo.client;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

import net.minecraft.client.KeyMapping;

import com.jg.memo.MemoMod;
import com.jg.memo.client.data.MemoStore;
import com.jg.memo.client.data.MemoSettingsStore;
import com.jg.memo.client.hud.PinnedMemoHud;
import com.jg.memo.client.screen.MemoScreen;

import org.lwjgl.glfw.GLFW;

public class MemoModClient implements ClientModInitializer {
	public static KeyMapping openKey;

	@Override
	public void onInitializeClient() {
		KeyMapping.Category category = KeyMapping.Category.register(MemoMod.id("memo"));
		openKey = KeyMappingHelper.registerKeyMapping(
				new KeyMapping("key.memo.open", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, category));

		// Load (and later save) the memo file.
		MemoStore.get();
		MemoSettingsStore.get();

		// Small pinned memo overlay, drawn with vanilla HUD sprites.
		HudElementRegistry.addLast(MemoMod.id("pinned_memo"), PinnedMemoHud::render);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openKey.consumeClick()) {
				if (client.gui.screen() == null) {
					client.gui.setScreen(new MemoScreen());
				} else if (client.gui.screen() instanceof MemoScreen) {
					client.gui.setScreen(null);
				}
			}
		});
	}
}
