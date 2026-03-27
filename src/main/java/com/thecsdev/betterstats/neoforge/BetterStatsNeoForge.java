package com.thecsdev.betterstats.neoforge;

import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.neoforge.client.BetterStatsNeoClient;
import com.thecsdev.betterstats.neoforge.server.BetterStatsNeoServer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(BetterStats.MOD_ID)
public final class BetterStatsNeoForge
{
	// ==================================================
	public BetterStatsNeoForge()
	{
		//create an instance of the mod's main class, depending on the dist
		switch(FMLEnvironment.getDist())
		{
			case CLIENT           -> new BetterStatsNeoClient();
			case DEDICATED_SERVER -> new BetterStatsNeoServer();
		}
	}
	// ==================================================
}
