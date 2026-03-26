package com.thecsdev.betterstats.fabric;

import com.thecsdev.betterstats.fabric.client.BetterStatsFabricClient;
import com.thecsdev.betterstats.fabric.server.BetterStatsFabricServer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;

public final class BetterStatsFabric implements ModInitializer, ClientModInitializer, DedicatedServerModInitializer
{
	// ==================================================
	public final @Override void onInitializeClient() { new BetterStatsFabricClient(); }
	public final @Override void onInitializeServer() { new BetterStatsFabricServer(); }
	// --------------------------------------------------
	public final @Override void onInitialize() {}
	// ==================================================
}
