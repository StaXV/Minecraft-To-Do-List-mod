package com.staxv.memo.client.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import com.staxv.memo.client.screen.MemoSettingsScreen;

public class MemoModMenuApi implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return MemoSettingsScreen::new;
	}
}
