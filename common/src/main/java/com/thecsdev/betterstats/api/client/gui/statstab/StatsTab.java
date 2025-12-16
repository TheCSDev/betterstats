package com.thecsdev.betterstats.api.client.gui.statstab;

import com.thecsdev.betterstats.api.client.gui.screen.IBetterStatsGui;
import com.thecsdev.betterstats.api.mcbs.McbsStats;
import com.thecsdev.betterstats.client.gui.statstab.StatsTabGeneral;
import com.thecsdev.common.util.annotations.Virtual;
import com.thecsdev.common.util.collections.GenericProperties;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.widget.TDropdownWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

/**
 * Represents a "statistics tab" in a {@link IBetterStatsGui}.<br>
 * Examples include "General", "Items", and "Mobs" tabs.
 */
public abstract class StatsTab implements TDropdownWidget.Entry
{
	// ================================================== ==================================================
	//                                           StatsTab IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * Returns the display name of this {@link StatsTab}.<br>
	 * This {@link Component} will be rendered in the GUI to indicate the tab's name.
	 */
	public abstract @NotNull Component getDisplayName();
	// --------------------------------------------------
	/**
	 * Invoked when a {@link StatsTab}'s filters GUI is initializing.
	 * @param context The {@link FiltersInitContext}.
	 */
	public @Virtual void initFilters(FiltersInitContext context) {
		StatsTabUtils.initDefaultFilters(context);
	}

	/**
	 * Invoked when the {@link StatsTab}'s statistics GUI is initializing.
	 * @param context The {@link StatsInitContext}.
	 */
	public abstract void initStats(StatsInitContext context);
	// ==================================================
	/**
	 * Returns the {@link StatsTab} instance that is selected by default.
	 */
	public static final @NotNull StatsTab getHomePage() { return StatsTabGeneral.INSTANCE; }
	// ================================================== ==================================================
	//                                        StatFilters IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link GenericProperties} implementation that holds "Filters" configuration that
	 * is used to filter statistics displayed by a {@link StatsInitContext}.
	 * <p>
	 * These filters are defined and configured by {@link FiltersInitContext}s.
	 */
	public static final class Filters extends GenericProperties<Identifier> {
		private static final @Serial long serialVersionUID = -4850404348614043298L;
	}
	// ================================================== ==================================================
	//                                 FiltersInitContext IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * The initialization context for when a {@link StatsTab}'s corresponding "stat
	 * filters" GUI is initializing. Use this to create your own stat filters GUI
	 * for this {@link StatsTab}.
	 */
	public static interface FiltersInitContext
	{
		// ==================================================
		/**
		 * The {@link TPanelElement} onto which the filters GUI is to be initialized.
		 */
		public @NotNull TPanelElement getPanel();
		// --------------------------------------------------
		/**
		 * The {@link Filters} configuration for filtering statistics displayed
		 * by a {@link StatsInitContext}.
		 */
		public @NotNull StatsTab.Filters getFilters();
		// --------------------------------------------------
		/**
		 * Returns the {@link IBetterStatsGui}'s currently selected {@link StatsTab}.
		 */
		public @NotNull StatsTab getStatsTab();

		/**
		 * Sets the {@link IBetterStatsGui}'s currently selected {@link StatsTab}.
		 * @param tab The {@link StatsTab} to use.
		 * @throws NullPointerException When the argument is {@code null}.
		 */
		public void setStatsTab(@NotNull StatsTab tab) throws NullPointerException;
		// --------------------------------------------------
		/**
		 * Refreshes the currently selected {@link StatsTab}'s "statistics"
		 * panel with a new {@link StatsInitContext} by reinitializing it
		 * using the current filter settings.
		 * <p>
		 * Call this method after making any changes in {@link #getFilters()}.
		 *
		 * @apiNote To anyone making a custom implementation of this method, remember
		 * that changes to the selected {@link StatsTab} should reinitialize the entire
		 * {@link IBetterStatsGui}. Otherwise, only reinitialize the "statistics" panel.
		 */
		public void applyFilters();
		// ==================================================
	}
	// ================================================== ==================================================
	//                                   StatsInitContext IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * The initialization context for when a {@link StatsTab}'s "statistics" GUI is
	 * initializing. Use this to create your own statistics GUI.
	 */
	public static interface StatsInitContext
	{
		// ==================================================
		/**
		 * The {@link TPanelElement} onto which the statistics GUI is to be initialized.
		 */
		public @NotNull TPanelElement getPanel();

		/**
		 * The {@link Filters} configuration for filtering statistics displayed
		 * by a {@link StatsInitContext}.
		 */
		public @NotNull StatsTab.Filters getFilters();

		/**
		 * The {@link McbsStats} instance holding statistics data
		 * that is to be displayed on screen.
		 */
		public @NotNull McbsStats getStatsProvider();
		// --------------------------------------------------
		/**
		 * Refreshes the "statistics" GUI by reinitializing it.
		 */
		public void refresh();
		// ==================================================
	}
	// ================================================== ==================================================
}
