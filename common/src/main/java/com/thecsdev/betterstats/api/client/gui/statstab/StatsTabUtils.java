package com.thecsdev.betterstats.api.client.gui.statstab;

import com.thecsdev.betterstats.api.client.gui.screen.IBetterStatsGui;
import com.thecsdev.betterstats.api.client.registry.BClientRegistries;
import com.thecsdev.betterstats.resources.BSSLang;
import com.thecsdev.common.math.Bounds2i;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.tooltip.TTooltip;
import com.thecsdev.commonmc.api.client.gui.widget.TCheckboxWidget;
import com.thecsdev.commonmc.api.client.gui.widget.TDropdownWidget;
import com.thecsdev.commonmc.api.client.gui.widget.stats.TCustomStatWidget;
import com.thecsdev.commonmc.api.client.gui.widget.stats.TEntityStatsWidget;
import com.thecsdev.commonmc.api.client.gui.widget.stats.TItemStatsWidget;
import com.thecsdev.commonmc.api.client.gui.widget.text.TSimpleTextFieldWidget;
import com.thecsdev.commonmc.api.stats.util.CustomStat;
import com.thecsdev.commonmc.api.stats.util.EntityStats;
import com.thecsdev.commonmc.api.stats.util.ItemStats;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

import static com.thecsdev.betterstats.BetterStats.MOD_ID;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

/**
 * Utilities for initializing {@link StatsTab} GUI interfaces.
 */
public final class StatsTabUtils
{
	// ==================================================
	/**
	 * The default horizontal and vertical margin that is applied to
	 * various GUI elements on an {@link IBetterStatsGui}.
	 */
	public static final int GAP = 3;
	// --------------------------------------------------
	/**
	 * {@link StatsTab.Filters} key for the "Search..." filter.
	 * <p>
	 * <b>Filter value type:</b> {@link String}
	 */
	public static final Identifier FID_SEARCH = fromNamespaceAndPath(MOD_ID, "search_query");

	/**
	 * {@link StatsTab.Filters} key for the "Show empty stats" filter.
	 * <p>
	 * <b>Filter value type:</b> {@link Boolean}
	 */
	public static final Identifier FID_EMPTYSTATS = fromNamespaceAndPath(MOD_ID, "show_empty_stats");
	// ==================================================
	private StatsTabUtils() {}
	// ==================================================
	/**
	 * Calculates and returns the bounding-box that is to be applied to the
	 * next vertically-placed child {@link TElement} on a given {@link TPanelElement}.
	 * @param panel The target {@link TPanelElement}.
	 * @param height The {@link Bounds2i#height} that will be used.
	 * @throws NullPointerException If the argument is {@code null}.
	 * @see TPanelElement#computeNextYBounds(int, int)
	 */
	public static final Bounds2i nextYBounds(@NotNull TPanelElement panel, int height) throws NullPointerException {
		return panel.computeNextYBounds(height, GAP);
	}
	// ==================================================
	/**
	 * Creates and adds a {@link TLabelElement} to the {@link TPanelElement},
	 * positioned at the bottom of the panel's {@link TElement#getContentBounds()},
	 * and taking up full panel width.
	 * @param panel The target {@link TPanelElement}.
	 * @param text The label's text.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public static final TLabelElement initGroupLabel(@NotNull TPanelElement panel, @NotNull Component text) throws NullPointerException {
		final var label = new TLabelElement();
		label.setBounds(nextYBounds(panel, 17));
		label.textProperty().set(text, StatsTabUtils.class);
		label.textColorProperty().set(0xFFFFFF00, StatsTabUtils.class);
		panel.add(label);
		return label;
	}
	// ==================================================
	/**
	 * Initializes the default user-interface for a {@link StatsTab}'s "Filters" panel.
	 * @param context The {@link StatsTab.FiltersInitContext}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public static final void initDefaultFilters(@NotNull StatsTab.FiltersInitContext context) throws NullPointerException
	{
		//obtain the panel
		final var panel = context.getPanel();
		/*create the "Filters" group label*/ {
			final var lbl = initGroupLabel(panel, BSSLang.gui_statstab_filters());
			lbl.setBounds(lbl.getBounds().add(0, 0, 0, 8));
			lbl.textColorProperty().set(-1, StatsTabUtils.class);
			lbl.textAlignmentProperty().set(CompassDirection.CENTER, StatsTabUtils.class);
		}
		//create the rest
		initStatsTabFilter(context);
		initSearchFilter(context);
		initShowAllStatsFilter(context);
	}
	// --------------------------------------------------
	/**
	 * Initializes the {@link StatsTab} selection dropdown widget.
	 * @param context The {@link StatsTab.FiltersInitContext}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public static final void initStatsTabFilter(final @NotNull StatsTab.FiltersInitContext context) throws NullPointerException
	{
		//create and add the widget
		final var panel = context.getPanel();
		final var widget = new TDropdownWidget<StatsTab>();
		widget.setBounds(nextYBounds(panel, 20));
		widget.tooltipProperty().set(__ -> TTooltip.of(BSSLang.gui_statstab_filter_selectedTab()), StatsTabUtils.class);
		panel.add(widget);

		//add entries to the widget
		final var entries = widget.getEntries();
		for(final var st : BClientRegistries.STATS_TAB.entrySet())
			entries.add(st.getValue());

		//set currently selected tab, and then add change listener
		widget.selectedEntryProperty().set(context.getStatsTab(), StatsTabUtils.class);
		widget.selectedEntryProperty().addChangeListener((p, o, n) -> {
			context.setStatsTab(n);
			context.applyFilters();
		});
	}

	/**
	 * Initializes the {@link TSimpleTextFieldWidget} "Search" filter that filters
	 * statistics based on their names.
	 * @param context The {@link StatsTab.FiltersInitContext}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public static final void initSearchFilter(final @NotNull StatsTab.FiltersInitContext context) throws NullPointerException
	{
		//create and add the widget
		final var panel = context.getPanel();
		final var widget = new TSimpleTextFieldWidget();
		widget.setBounds(nextYBounds(panel, 20));
		widget.placeholderProperty().set(BSSLang.gui_statstab_filter_search(), StatsTabUtils.class);
		panel.add(widget);

		//set up initial value and change listeners
		widget.textProperty().set(
				context.getFilters().getProperty(String.class, FID_SEARCH, ""),
				StatsTabUtils.class);
		widget.textProperty().addChangeListener((p, o, n) -> {
			context.getFilters().setProperty(String.class, FID_SEARCH, n);
			context.applyFilters();
		});
	}

	/**
	 * Initializes the {@link TCheckboxWidget} "Show all stats" filter that shows
	 * statistics whose values are "0".
	 * @param context The {@link StatsTab.FiltersInitContext}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public static final void initShowAllStatsFilter(final @NotNull StatsTab.FiltersInitContext context) throws NullPointerException
	{
		//preparation and math
		final var panel    = context.getPanel();
		final var nextRect = nextYBounds(panel, 20);

		//create and add the checkbox
		final var checkbox = new TCheckboxWidget();
		checkbox.setBounds(nextRect.x, nextRect.y, 20, nextRect.height);
		panel.add(checkbox);

		//create and add the label
		final var label    = new TLabelElement();
		label.setBounds(nextRect.x + 25, nextRect.y, nextRect.width - 25, nextRect.height);
		label.setText(BSSLang.gui_statstab_filter_showAllStats());
		panel.add(label);

		//set up initial value and change listeners
		checkbox.checkedProperty().set(
				context.getFilters().getProperty(Boolean.class, FID_EMPTYSTATS, false),
				StatsTabUtils.class);
		checkbox.checkedProperty().addChangeListener((p, o, n) -> {
			context.getFilters().setProperty(Boolean.class, FID_EMPTYSTATS, n);
			context.applyFilters();
		});
	}
	// ==================================================
	/**
	 * Initializes a collection of "General" statistics in a given "group".
	 * @param panel The {@link TPanelElement} where the stats initialize.
	 * @param groupLabel The display name of the group of statistics.
	 * @param customStats The "General" statistics collection.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 */
	public static final void initGeneralStats(
			@NotNull TPanelElement panel,
			@NotNull Component groupLabel,
			@NotNull Iterable<CustomStat> customStats) throws NullPointerException {
		initGeneralStats(panel, groupLabel, customStats, null);
	}

	/**
	 * Initializes a collection of "General" statistics in a given "group".
	 * @param panel The {@link TPanelElement} where the stats initialize.
	 * @param groupLabel The display name of the group of statistics.
	 * @param customStats The "General" statistics collection.
	 * @param widgetPostProcessor An optional post-processor for each created {@link TCustomStatWidget}.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 */
	public static final void initGeneralStats(
			@NotNull TPanelElement panel,
			@NotNull Component groupLabel,
			@NotNull Iterable<CustomStat> customStats,
			@Nullable Consumer<TCustomStatWidget> widgetPostProcessor) throws NullPointerException
	{
		//not null requirements
		Objects.requireNonNull(panel);
		Objects.requireNonNull(groupLabel);
		Objects.requireNonNull(customStats);

		//obtain iterator instance, cancel if there are no entries
		final var iterator = customStats.iterator();
		if(!iterator.hasNext()) return;

		//initialize group label
		initGroupLabel(panel, groupLabel);

		//initialize stats
		var nextBounds = nextYBounds(panel, 18);
		while(iterator.hasNext()) {
			final var widget = new TCustomStatWidget(iterator.next());
			widget.setBounds(nextBounds);
			if(widgetPostProcessor != null) //right before adding to panel
				widgetPostProcessor.accept(widget);
			panel.add(widget);
			nextBounds = nextBounds.add(0, nextBounds.height + GAP, 0, 0);
		}
	}
	// --------------------------------------------------
	/**
	 * Initializes a collection of "Item" statistics in a given "group".
	 * @param panel The {@link TPanelElement} where the stats initialize.
	 * @param groupLabel The display name of the group of statistics.
	 * @param itemStats The "Item" statistics collection.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 */
	public static final void initItemStats(
			@NotNull TPanelElement panel,
			@NotNull Component groupLabel,
			@NotNull Iterable<ItemStats> itemStats) throws NullPointerException {
		initItemStats(panel, groupLabel, itemStats, null);
	}

	/**
	 * Initializes a collection of "Item" statistics in a given "group".
	 * @param panel The {@link TPanelElement} where the stats initialize.
	 * @param groupLabel The display name of the group of statistics.
	 * @param itemStats The "Item" statistics collection.
	 * @param widgetPostProcessor An optional post-processor for each created {@link TItemStatsWidget}.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 */
	public static final void initItemStats(
			@NotNull TPanelElement panel,
			@NotNull Component groupLabel,
			@NotNull Iterable<ItemStats> itemStats,
			@Nullable Consumer<TItemStatsWidget> widgetPostProcessor) throws NullPointerException
	{
		//not null requirements
		Objects.requireNonNull(panel);
		Objects.requireNonNull(groupLabel);
		Objects.requireNonNull(itemStats);

		//obtain iterator instance, cancel if there are no entries
		final var iterator = itemStats.iterator();
		if(!iterator.hasNext()) return;

		//initialize group label
		initGroupLabel(panel, groupLabel);

		//prepare maths to initialize stats
		int size = 20; //widget width and height
		int startX, endX, nextX, nextY; {
			final var nextBounds = nextYBounds(panel, 0);
			startX = nextX = nextBounds.x;
			endX   = nextBounds.endX;
			nextY  = nextBounds.y;
		}

		//initialize stat widgets
		while(iterator.hasNext())
		{
			//create and add the widget
			final var widget = new TItemStatsWidget(iterator.next());
			widget.setBounds(nextX, nextY, size, size);
			if(widgetPostProcessor != null) //right before adding to panel
				widgetPostProcessor.accept(widget);
			panel.add(widget);
			//increment position values
			nextX += size + GAP;
			if(nextX + size > endX) { nextX = startX; nextY += size + GAP; }
		}
	}
	// --------------------------------------------------
	/**
	 * Initializes a collection of "Mob" statistics in a given "group".
	 * @param panel The {@link TPanelElement} where the stats initialize.
	 * @param groupLabel The display name of the group of statistics.
	 * @param mobStats The "Mob" statistics collection.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 */
	public static final void initMobStats(
			@NotNull TPanelElement panel,
			@NotNull Component groupLabel,
			@NotNull Iterable<EntityStats> mobStats) throws NullPointerException {
		initMobStats(panel, groupLabel, mobStats, null);
	}

	/**
	 * Initializes a collection of "Mob" statistics in a given "group".
	 * @param panel The {@link TPanelElement} where the stats initialize.
	 * @param groupLabel The display name of the group of statistics.
	 * @param mobStats The "Mob" statistics collection.
	 * @param widgetPostProcessor An optional post-processor for each created {@link TEntityStatsWidget}.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 */
	public static final void initMobStats(
			@NotNull TPanelElement panel,
			@NotNull Component groupLabel,
			@NotNull Iterable<EntityStats> mobStats,
			@Nullable Consumer<TEntityStatsWidget> widgetPostProcessor) throws NullPointerException
	{
		//not null requirements
		Objects.requireNonNull(panel);
		Objects.requireNonNull(groupLabel);
		Objects.requireNonNull(mobStats);

		//obtain iterator instance, cancel if there are no entries
		final var iterator = mobStats.iterator();
		if(!iterator.hasNext()) return;

		//initialize group label
		initGroupLabel(panel, groupLabel);

		//prepare maths to initialize stats
		int size = 30; //widget width and height
		int startX, endX, nextX, nextY; {
			final var nextBounds = nextYBounds(panel, 0);
			startX = nextX = nextBounds.x;
			endX   = nextBounds.endX;
			nextY  = nextBounds.y;
		}

		//initialize stat widgets
		while(iterator.hasNext())
		{
			//create and add the widget
			final var widget = new TEntityStatsWidget(iterator.next());
			widget.setBounds(nextX, nextY, size, size);
			if(widgetPostProcessor != null) //right before adding to panel
				widgetPostProcessor.accept(widget);
			panel.add(widget);
			//increment position values
			nextX += size + GAP;
			if(nextX + size > endX) { nextX = startX; nextY += size + GAP; }
		}
	}
	// ==================================================
}
