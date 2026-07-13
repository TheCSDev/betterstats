package com.thecsdev.betterstats.client;

import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.api.client.gui.screen.BetterStatsScreen;
import com.thecsdev.betterstats.api.client.registry.BClientRegistries;
import com.thecsdev.betterstats.api.mcbs.view.tab.McbsEditorTabGUI;
import com.thecsdev.commonmc.api.client.events.ClientEvent;
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
	private static long LAST_LOGIN_TIME = System.currentTimeMillis();
	// ==================================================
	public BetterStatsClient()
	{
		//initialize and register features
		BClientRegistries.bootstrap();
		McbsEditorTabGUI.bootstrap();

		//modify the "Statistics" button on the game's pause screen
		registerVanillaButtonMod(PauseScreen.class, translatable("gui.stats"), (_, vanillaOnClick) -> {
			//if the user is holding down "Shift", run vanilla button functionality
			if(TGuiUtils.isShiftDown()) vanillaOnClick.run();
			//else open the Better Statistics Screen
			else {
				final var client = Minecraft.getInstance();
				client.gui.setScreen(new BetterStatsScreen(client.gui.screen()).getAsScreen());
			}
		});

		//keep track of last login time
		ClientEvent.PLAYER_JOIN.addListener(_ -> LAST_LOGIN_TIME = System.currentTimeMillis());
		ClientEvent.PLAYER_QUIT.addListener(_ -> LAST_LOGIN_TIME = System.currentTimeMillis());

		//FIXME - REMOVE TEST CODE THAT POPULATES STATS WITH ARBITRARY NUMBERS:
		/*
		ClientEvent.PLAYER_JOIN.addListener(_ ->
		{
			//obtain singleplayer server
			final var server = Minecraft.getInstance().getSingleplayerServer();
			if(server == null) return;

			//continue execution on server-side
			server.execute(() ->
			{
				//obtain the first player (who is usually the main player)
				final var player = server.getPlayerList().getPlayers().stream().findFirst().orElse(null);
				if(player == null) return;
				final var stats  = player.getStats();
				final var rng    = new Random();

				//noinspection unchecked | award every single stat in the game to the player
				for(final var statType : (Registry<StatType<Object>>)(Object) BuiltInRegistries.STAT_TYPE)
				{
					//skip "custom/general" statistics
					if(((Object) statType) == Stats.CUSTOM) continue;
					//populate all other stat types
					for(final var statSubject : statType.getRegistry())
						stats.setValue(player, statType.get(statSubject), rng.nextInt(0, 1024));
				}
			});
		});
		*/
	}
	// ==================================================
	/**
	 * The last time the player logged in, in milliseconds since the epoch.
	 * This is updated whenever the player joins or leaves a world.
	 */
	public static final long getLastLoginTime() { return LAST_LOGIN_TIME; }
	// ==================================================
}
