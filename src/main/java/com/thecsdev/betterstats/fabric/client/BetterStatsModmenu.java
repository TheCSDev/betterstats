package com.thecsdev.betterstats.fabric.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.api.client.gui.screen.BetterStatsConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.ApiStatus;

/**
 * {@link ModMenuApi} implementation for {@link BetterStats}.
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class BetterStatsModmenu implements ModMenuApi
{
	// ==================================================
	public final @Override ConfigScreenFactory<?> getModConfigScreenFactory() {
		return lastScreen -> new BetterStatsConfigScreen(lastScreen).getAsScreen();
	}
	// ==================================================
}
