package com.thecsdev.betterstats.api.mcbs.view.statsview;

import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorTab;
import com.thecsdev.betterstats.api.mcbs.model.McbsStats;
import com.thecsdev.betterstats.api.mcbs.view.McbsEditorGUI;
import com.thecsdev.betterstats.mcbs.view.statsview.*;
import com.thecsdev.common.util.annotations.Virtual;
import com.thecsdev.common.util.collections.GenericProperties;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.widget.TDropdownWidget;
import com.thecsdev.commonmc.api.stats.IStatsProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;

import static com.thecsdev.betterstats.BetterStats.MOD_ID;
import static com.thecsdev.betterstats.api.client.registry.BClientRegistries.STATS_VIEW;
import static com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils.*;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

/**
 * Represents a 'view' within an {@link McbsEditorTab} of an {@link McbsEditorGUI}.
 * Each view is responsible for displaying a specific category of statistics, such
 * as general stats, item-related stats, or mob-related stats.
 * <p>
 * This abstract {@link Class} serves as a blueprint for creating different types
 * of statistics views. It is the primary thing responsible for constructing GUI
 * interfaces for statistics.
 */
@Environment(EnvType.CLIENT)
public abstract class StatsView implements TDropdownWidget.Entry
{
	// ================================================== ==================================================
	//                                          StatsView IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * Returns the {@link StatsView} instance that is used by default, usually
	 * as the introductory view that is open when an {@link McbsEditorTab} tab
	 * is opened for its first time.
	 */
	public static final @NotNull StatsView getDefault() { return StatsViewGeneral.INSTANCE; }
	// ==================================================
	/**
	 * Returns the display name of this {@link StatsView}.<br>
	 * This {@link Component} will be shown on the GUI to indicate the view's name.
	 */
	public abstract @NotNull Component getDisplayName();
	// --------------------------------------------------
	/**
	 * Invoked when a {@link StatsView}'s filters GUI is initializing.
	 * @param context The {@link FiltersInitContext}.
	 */
	public @Virtual void initFilters(@NotNull FiltersInitContext context) {
		StatsViewUtils.initDefaultFilters(context);
		initSearchFilter(context);
		initShowAllStatsFilter(context);
	}

	/**
	 * Invoked when the {@link StatsView}'s statistics GUI is initializing.
	 * @param context The {@link StatsInitContext}.
	 */
	public abstract void initStats(@NotNull StatsInitContext context);
	// ==================================================
	/**
	 * Registers internal {@link StatsView} instances.
	 */
	public static final @ApiStatus.Internal void bootstrap()
	{
		STATS_VIEW.put(fromNamespaceAndPath(MOD_ID, "general"), StatsViewGeneral.INSTANCE);
		STATS_VIEW.put(fromNamespaceAndPath(MOD_ID, "items"),   StatsViewItems.INSTANCE);
		STATS_VIEW.put(fromNamespaceAndPath(MOD_ID, "mobs"),    StatsViewMobs.INSTANCE);
		STATS_VIEW.put(fromNamespaceAndPath(MOD_ID, "food"),    StatsViewFood.INSTANCE);
		STATS_VIEW.put(fromNamespaceAndPath(MOD_ID, "hunter"),  StatsViewHunter.INSTANCE);
	}
	// ================================================== ==================================================
	//                                            Filters IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link GenericProperties} implementation that holds "Filters" configuration that
	 * is used to filter statistics displayed with {@link StatsInitContext}s.
	 * <p>
	 * These filters are defined and managed with {@link FiltersInitContext}s.
	 */
	public static @ApiStatus.NonExtendable class Filters extends GenericProperties<Identifier> {
		private static final @Serial long serialVersionUID = -4850404348614043298L;
	}
	// ================================================== ==================================================
	//                                 FiltersInitContext IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * The initialization context for when a {@link StatsView}'s "stat filters"
	 * GUI is initializing. Use this to create your own stat filters GUI for
	 * this {@link StatsView}.
	 */
	public static @ApiStatus.NonExtendable interface FiltersInitContext
	{
		// ==================================================
		/**
		 * The {@link TPanelElement} onto which the filters GUI is to be initialized.
		 */
		public @NotNull TPanelElement getPanel();
		// --------------------------------------------------
		/**
		 * The {@link Filters} configurations for filtering statistics displayed
		 * with {@link StatsInitContext}s.
		 */
		public @NotNull StatsView.Filters getFilters();
		// ==================================================
		/**
		 * Utility method for getting the value of the {@link StatsViewUtils#FID_STATSVIEW} filter.
		 * @see #getFilters()
		 */
		default @ApiStatus.NonExtendable @NotNull StatsView getStatsView() {
			return getFilters().getProperty(StatsView.class, FID_STATSVIEW, StatsView.getDefault());
		}

		/**
		 * Utility method for setting the value of the {@link StatsViewUtils#FID_STATSVIEW} filter.
		 * @param statsView The {@link StatsView} value to set.
		 * @see #getFilters()
		 */
		default @ApiStatus.NonExtendable void setStatsView(@Nullable StatsView statsView) {
			getFilters().setProperty(StatsView.class, FID_STATSVIEW, statsView);
		}
		// ==================================================
	}
	// ================================================== ==================================================
	//                                   StatsInitContext IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * The initialization context for when a {@link StatsView}'s "statistics"
	 * GUI is initializing. Use this to create your own statistics GUI.
	 */
	public static @ApiStatus.NonExtendable interface StatsInitContext
	{
		// ==================================================
		/**
		 * The {@link TPanelElement} onto which the statistics GUI is to be initialized.
		 */
		public @NotNull TPanelElement getPanel();
		// --------------------------------------------------
		/**
		 * The {@link Filters} configuration for filtering statistics displayed
		 * with {@link StatsInitContext}s.
		 */
		public @NotNull StatsView.Filters getFilters();

		/**
		 * The {@link McbsStats} instance holding the statistics data. Use this to
		 * visualize the statistics on the GUI.
		 * <p>
		 * <b><u>Important API note:</u></b><br>
		 * Intended to be <b>read-only</b>! Attempts to set stat values may and likely
		 * will {@code throw}!
		 */
		public @NotNull IStatsProvider getStats();
		// ==================================================
	}
	// ================================================== ==================================================
}
