package com.thecsdev.betterstats.api.client.registry;

import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.api.client.gui.statsview.StatsView;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link BetterStats}'s client-sided registries for adding features to the mod.
 * <p>
 * Note that this does not use the game's native {@link Registry} system, instead
 * relying on {@link Map}s for simplicity.
 */
public class BClientRegistries
{
	// ==================================================
	private BClientRegistries() {}
	// ==================================================
	/**
	 * {@link Map} of registered {@link StatsView}s.
	 * <p>
	 * {@link Map.Entry#getKey()} = ID of registered feature<br>
	 * {@link Map.Entry#getValue()} = Registered {@link NotNull} {@link StatsView}
	 */
	public static final Map<@NotNull Identifier, @NotNull StatsView> STATS_TAB = new LinkedHashMap<>();
	// ==================================================
}
