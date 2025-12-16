package com.thecsdev.betterstats.api.client.gui.screen;

import com.thecsdev.betterstats.api.client.gui.statstab.StatsTab;
import com.thecsdev.betterstats.api.mcbs.McbsFile;
import com.thecsdev.betterstats.client.gui.BetterStatsPanel;
import com.thecsdev.commonmc.api.client.gui.TElement;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An {@code interface} that provides contextual information to a {@link BetterStatsPanel}.
 */
public @ApiStatus.NonExtendable interface IBetterStatsGui
{
	// ==================================================
	/**
	 * @see TElement#getClient()
	 */
	public @Nullable Minecraft getClient();

	/**
	 * Refreshes the entire {@link IBetterStatsGui} by reinitializing
	 * all of its GUI.
	 * @see TElement#clearAndInit()
	 */
	public void refresh();
	// ==================================================
	/**
	 * Returns the {@link McbsFile} this {@link IBetterStatsGui} uses.
	 */
	public @NotNull McbsFile getMcbsFile();

	/**
	 * Sets the {@link McbsFile} that is to be used by this {@link IBetterStatsGui}.
	 * <p>
	 * This 'set' call does not automatically refresh the GUI. You must call
	 * {@link #refresh()} after this call.
	 *
	 * @param file The {@link McbsFile} instance.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public void setMcbsFile(@NotNull McbsFile file) throws NullPointerException;
	// --------------------------------------------------
	/**
	 * Returns the {@link StatsTab} currently used by this {@link IBetterStatsGui}.
	 */
	public @NotNull StatsTab getStatsTab();

	/**
	 * Sets the {@link StatsTab} that is to be used by this {@link IBetterStatsGui}.
	 * <p>
	 * This 'set' call does not automatically refresh the GUI. You must call
	 * {@link StatsTab.FiltersInitContext#applyFilters()} after this call.
	 *
	 * @param tab The {@link StatsTab} instance.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public void setStatsTab(@NotNull StatsTab tab) throws NullPointerException;
	// --------------------------------------------------
	/**
	 * The {@link StatsTab.Filters} configuration for filtering statistics displayed
	 * by a {@link StatsTab.StatsInitContext}.
	 */
	public @NotNull StatsTab.Filters getFilters();
	// ==================================================
}
