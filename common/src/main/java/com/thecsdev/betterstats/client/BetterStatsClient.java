package com.thecsdev.betterstats.client;

import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.api.client.gui.screen.BetterStatsScreen;
import com.thecsdev.betterstats.api.mcbs.view.menubar.MenubarItem;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.betterstats.api.mcbs.view.tab.McbsEditorTabGUI;
import com.thecsdev.commonmc.api.client.gui.util.TGuiUtils;
import dev.architectury.event.events.client.ClientPlayerEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.ClickEvent;

import java.net.URI;
import java.nio.file.Path;
import java.util.Locale;

import static com.thecsdev.betterstats.resources.BSSLang.WATERMARK;
import static com.thecsdev.commonmc.api.client.hooks.GuiHooks.registerVanillaButtonMod;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;

/**
 * The main "client" entry-point for this mod, that is executed
 * by all loaders (fabric/neoforge).
 */
public class BetterStatsClient extends BetterStats
{
	// ==================================================
	private static boolean flag_20260106 = false;
	// ==================================================
	public BetterStatsClient()
	{
		//register features
		MenubarItem.bootstrap();
		StatsView.bootstrap();
		McbsEditorTabGUI.bootstrap();

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

		//public service announcement
		ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(localPlayer ->
		{
			//do nothing if PSA messages are not allowed
			if(!getConfig().allowsChatPsaMessages()) return;
			//date-based kill-switch for this PSA
			else if(System.currentTimeMillis() > 1769904000000L) return;
			//the psa is in english. do nothing if user OS is not english
			else if(!Locale.getDefault().getLanguage().equalsIgnoreCase("en")) return;

			//manage the one-time flag
			try {
				//backup in-memory flag
				if(flag_20260106) return; else flag_20260106 = true;

				//check if the flag file already exists. if it does - return and do nothing
				final var flagFile = Path.of(System.getProperty("user.home"), ".cache",
						"thecsdev", "betterstats", "flags", "20260106-hytale").toFile();
				if(flagFile.exists()) return;

				//else create the flag file so next time it is spotted
				flagFile.getParentFile().mkdirs();
				flagFile.createNewFile();
			}
			catch(Exception e) { return; /*if something goes wrong, abort*/ }

			//pre-construct some literals
			final var txt_clickHere = literal("Learn more.")
					.withStyle(ChatFormatting.AQUA);
			txt_clickHere.setStyle(txt_clickHere.getStyle().withClickEvent(
					new ClickEvent.OpenUrl(URI.create("https://thecsdev.com/article/20251211_140000000/"))
			).withUnderlined(true));

			//send the client the PSA message
			localPlayer.displayClientMessage(literal("")
					.append(literal("[PSA] ").withStyle(ChatFormatting.YELLOW))
					.append(WATERMARK)
					.append(" If you registered a ")
					.append(literal("Hytale").withStyle(ChatFormatting.GREEN))
					.append(" account, please opt-out of their arbitration clause.")
					.append(" ").append(txt_clickHere),
					false);
			localPlayer.displayClientMessage(
					literal("(This is a one-time message and it will not show up again.)")
							.withStyle(ChatFormatting.GRAY),
					false);
		});
	}
	// ==================================================
}
