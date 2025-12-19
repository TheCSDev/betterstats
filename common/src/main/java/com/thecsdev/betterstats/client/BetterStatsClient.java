package com.thecsdev.betterstats.client;

import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.api.client.gui.screen.BetterStatsScreen;
import com.thecsdev.betterstats.api.mcbs.view.menubar.MenubarItem;
import com.thecsdev.commonmc.api.client.gui.util.TGuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;

import static com.thecsdev.commonmc.api.client.hooks.GuiHooks.registerVanillaButtonMod;
import static net.minecraft.network.chat.Component.translatable;

/**
 * The main "client" entry-point for this mod, that is executed
 * by all loaders (fabric/neoforge).
 */
public class BetterStatsClient extends BetterStats
{
	// ==================================================
	public BetterStatsClient()
	{
		//register features
		MenubarItem.bootstrap();

		//modify the "Statistics" button on the game's pause screen
		registerVanillaButtonMod(PauseScreen.class, translatable("gui.stats"), (button, vanillaOnClick) -> {
			//if the user is holding down "Shift", run vanilla button functionality
			if(TGuiUtils.isShiftDown()) vanillaOnClick.run();
			//else open the Better Statistics Screen
			else {
				final var client = Minecraft.getInstance();
				client.setScreen(new BetterStatsScreen(client.screen).getAsScreen());
			}
		});
	}
	// ==================================================
	/*
	 * Opens a {@link ConfirmLinkScreen} that asks the user whether
	 * they want to open the specified URI.
	 * @param uri The URI to show.
	 * @param isTrusted Whether the URI is trusted.
	 *
	@SuppressWarnings("SameParameterValue")
	private static final void showUrlScreen(@NotNull String uri, boolean isTrusted)
	{
		//argument validity assertion
		Objects.requireNonNull(uri);
		//obtain client variables stuff
		final var client     = Objects.requireNonNull(Minecraft.getInstance());
		final var lastScreen = client.screen;
		//create and set the confirmation screen
		final var screen     = new ConfirmLinkScreen(accepted -> {
			if(accepted) Util.getPlatform().openUri(uri);
			client.setScreen(lastScreen);
		}, uri, isTrusted);
		client.setScreen(screen);
	}*/
	// ==================================================
}
