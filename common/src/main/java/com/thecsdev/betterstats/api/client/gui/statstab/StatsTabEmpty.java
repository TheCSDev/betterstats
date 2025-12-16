package com.thecsdev.betterstats.api.client.gui.statstab;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 * {@link StatsTab} instance that has no visual GUI.
 */
public final class StatsTabEmpty extends StatsTab
{
	// ==================================================
	public static final StatsTabEmpty INSTANCE = new StatsTabEmpty();
	// ==================================================
	private StatsTabEmpty() {}
	// ==================================================
	public final @Override @NotNull Component getDisplayName() { return Component.empty(); }
	public final @Override void initStats(StatsTab.StatsInitContext context) {}
	// ==================================================
}
