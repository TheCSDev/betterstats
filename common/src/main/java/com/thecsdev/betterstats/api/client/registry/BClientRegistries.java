package com.thecsdev.betterstats.api.client.registry;

import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.api.client.gui.screen.IBetterStatsGui;
import com.thecsdev.betterstats.api.client.gui.statstab.StatsTab;
import com.thecsdev.commonmc.api.client.gui.ctxmenu.TContextMenu;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * {@link BetterStats}'s client-sided registries for adding features to the mod.
 * <p>
 * Note that this does not use the game's native {@link Registry} system, instead
 * relying on {@link Map}s for simplicity.
 */
public final class BClientRegistries
{
	// ==================================================
	private BClientRegistries() {}
	// ==================================================
	/**
	 * {@link Map} of registered {@link StatsTab}s.<br>
	 * The {@link Map} key is the unique ID of the registered feature,
	 * whereas the value is the {@link NotNull} {@link StatsTab}.
	 */
	public static final Map<@NotNull Identifier, @NotNull StatsTab> STATS_TAB = new LinkedHashMap<>();

	/**
	 * Menubar buttons that are to be featured in {@link IBetterStatsGui}.<br>
	 * The {@link Map} key is the unique ID of the registered feature,
	 * whereas the value is a {@link Pair} consisting of:
	 * <ul>
	 *     <li>a {@link Component} representing the button's text, and</li>
	 *     <li>a {@link Function} that takes an instance of {@link IBetterStatsGui} and returns
	 *     a {@link TContextMenu} to be shown when the button is clicked.</li>
	 * </ul>
	 */
	public static final Map<@NotNull Identifier, @NotNull Pair<Component, Function<IBetterStatsGui, TContextMenu>>> MENUBAR_BUTTON = new LinkedHashMap<>();
	// ==================================================
}
