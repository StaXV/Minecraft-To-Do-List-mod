package com.jg.memo.client.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import com.jg.memo.client.screen.MemoSettingsScreen;

public class MemoModMenuApi implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return MemoSettingsScreen::new;
	}
}
