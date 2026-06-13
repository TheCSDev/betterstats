package com.thecsdev.betterstats.api.mcbs.view.statsview;

import com.thecsdev.betterstats.api.client.registry.BClientRegistries;
import com.thecsdev.betterstats.api.mcbs.view.McbsEditorGUI;
import com.thecsdev.betterstats.resource.BLanguage;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.misc.TFillColorElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.tooltip.TTooltip;
import com.thecsdev.commonmc.api.client.gui.widget.TCheckboxWidget;
import com.thecsdev.commonmc.api.client.gui.widget.TDropdownWidget;
import com.thecsdev.commonmc.api.client.gui.widget.stats.TBlockStatsWidget;
import com.thecsdev.commonmc.api.client.gui.widget.stats.TCustomStatWidget;
import com.thecsdev.commonmc.api.client.gui.widget.stats.TEntityStatsWidget;
import com.thecsdev.commonmc.api.client.gui.widget.stats.TItemStatsWidget;
import com.thecsdev.commonmc.api.client.gui.widget.text.TSimpleTextFieldWidget;
import com.thecsdev.commonmc.api.stats.util.BlockStats;
import com.thecsdev.commonmc.api.stats.util.CustomStat;
import com.thecsdev.commonmc.api.stats.util.EntityStats;
import com.thecsdev.commonmc.api.stats.util.ItemStats;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static com.thecsdev.betterstats.BetterStats.MOD_ID;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

/**
 * Utilities for initializing {@link StatsView} GUI interfaces.
 */
@Environment(EnvType.CLIENT)
public final class StatsViewUtils
{
	// ==================================================
	/**
	 * The default horizontal and vertical margin that is commonly used for
	 * various GUI elements on {@link McbsEditorGUI}s.
	 */
	public static final int GAP = 3;
	// --------------------------------------------------
	/**
	 * {@link StatsView.Filters} key for the "Current {@link StatsView}" filter.
	 * <p>
	 * <b>Filter value type:</b> {@link StatsView}
	 */
	public static final Identifier FID_STATSVIEW = fromNamespaceAndPath(MOD_ID, "statsview");

	/**
	 * {@link StatsView.Filters} key for the "Search..." filter.
	 * <p>
	 * <b>Filter value type:</b> {@link String}
	 */
	public static final Identifier FID_SEARCH = fromNamespaceAndPath(MOD_ID, "search_query");

	/**
	 * {@link StatsView.Filters} key for the "Show empty stats" filter.
	 * <p>
	 * <b>Filter value type:</b> {@link Boolean}
	 */
	public static final Identifier FID_EMPTYSTATS = fromNamespaceAndPath(MOD_ID, "show_empty_stats");
	// ==================================================
	private StatsViewUtils() {}
	// ==================================================
	/**
	 * Creates and adds a {@link TLabelElement} to the {@link TPanelElement},
	 * positioned at the bottom of the panel's {@link TElement#getContentBounds()},
	 * and taking up full panel width.
	 * @param panel The target {@link TPanelElement}.
	 * @param text The label's text.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	@Contract("_, _ -> new")
	public static final @NotNull TLabelElement initGroupLabel(
			@NotNull TPanelElement panel, @NotNull Component text)
			throws NullPointerException
	{
		//require not null for arguments
		Objects.requireNonNull(panel);
		Objects.requireNonNull(text);

		//create and add a new label
		final var label = new TLabelElement(text);
		label.wrapTextProperty().set(true, StatsViewUtils.class);
		label.textColorProperty().set(0xFFFFFF00, StatsViewUtils.class);
		label.setBounds(panel.computeNextYBounds(17, GAP));
		label.setBoundsToFitText(label.getBounds().width);
		label.setBounds(label.getBounds().add(0, 0, 0, 8));
		panel.add(label);
		return label;
	}

	/**
	 * Creates and adds a "framed" label to the {@link TPanelElement}, positioned
	 * at the bottom of the panel's {@link TElement#getContentBounds()},
	 * and taking up full panel width.
	 * <p>
	 * The "frame" is a {@link TFillColorElement} with the {@link TLabelElement} as
	 * its child.
	 * @param panel The target {@link TPanelElement}.
	 * @param text The label's text.
	 * @param textScale The label's text scale.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @see #initGroupLabel(TPanelElement, Component)
	 */
	@ApiStatus.Internal
	@ApiStatus.Experimental
	public static final @NotNull Map.Entry<TFillColorElement, TLabelElement> initGroupLabelFramed(
			@NotNull TPanelElement panel, @NotNull Component text, double textScale)
			throws NullPointerException
	{
		//background fill-color
		final var frame = new TFillColorElement(0xFF303030, 0xFF000000);
		frame.setBounds(panel.computeNextYBounds(1024, GAP));

		//the label
		final var label = new TLabelElement(text);
		label.wrapTextProperty().set(true, StatsViewUtils.class);
		label.textScaleProperty().set(Math.max(textScale, 0.1d), StatsViewUtils.class);
		label.textColorProperty().set(0xFFFFFF00, StatsViewUtils.class);
		label.setBounds(frame.getBounds().add(7, 7, -14, -14));
		label.setBoundsToFitText(label.getBounds().width);

		//finalize and add elements
		frame.setBounds(frame.getBounds().height(label.getBounds().height + 14));
		frame.add(label);
		panel.add(frame);
		return Map.entry(frame, label);
	}
	// ==================================================
	/**
	 * Initializes the default user-interface for a {@link StatsView}'s "Filters" panel.
	 * @param context The {@link StatsView.FiltersInitContext}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public static void initDefaultFilters(@NotNull StatsView.FiltersInitContext context)
	{
		//obtain the panel
		final var panel = context.getPanel();
		//create the "Filters" group label
		final var lbl_group = initGroupLabel(panel, BLanguage.gui_statsview_filters());
		lbl_group.setBounds(lbl_group.getBounds().add(0, 0, 0, 8));
		lbl_group.textColorProperty().set(-1, StatsViewUtils.class);
		lbl_group.textAlignmentProperty().set(CompassDirection.CENTER, StatsViewUtils.class);
		//create the rest
		initStatsViewFilter(context);
	}
	// --------------------------------------------------
	/**
	 * Initializes the {@link StatsView} selection dropdown widget.
	 * @param context The {@link StatsView.FiltersInitContext}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public static final void initStatsViewFilter(final @NotNull StatsView.FiltersInitContext context) throws NullPointerException
	{
		//create and add the widget
		final var panel = context.getPanel();
		final var widget = new TDropdownWidget<StatsView>();
		widget.setBounds(panel.computeNextYBounds(20, GAP));
		widget.tooltipProperty().set(__ -> TTooltip.of(BLanguage.gui_statsview_filter_selectedView()), StatsViewUtils.class);
		panel.add(widget);

		//add entries to the widget
		final var entries = widget.getEntries();
		for(final var st : BClientRegistries.STATS_VIEW)
			entries.add(st);

		//set currently selected tab, and then add change listener
		widget.selectedEntryProperty().set(context.getStatsView(), StatsViewUtils.class);
		widget.selectedEntryProperty().addChangeListener((p, o, n) ->
				context.setStatsView(n));
	}

	/**
	 * Initializes the {@link TSimpleTextFieldWidget} "Search" filter that filters
	 * statistics based on their names.
	 * @param context The {@link StatsView.FiltersInitContext}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public static final void initSearchFilter(final @NotNull StatsView.FiltersInitContext context) throws NullPointerException
	{
		//create and add the widget
		final var panel = context.getPanel();
		final var widget = new TSimpleTextFieldWidget();
		widget.setBounds(panel.computeNextYBounds(20, GAP));
		widget.placeholderProperty().set(BLanguage.gui_statsview_filter_search(), StatsViewUtils.class);
		panel.add(widget);

		//set up initial value and change listeners
		widget.textProperty().set(
				context.getFilters().getProperty(String.class, FID_SEARCH, ""),
				StatsViewUtils.class);
		widget.textProperty().addChangeListener((p, o, n) ->
				context.getFilters().setProperty(String.class, FID_SEARCH, n));
	}

	/**
	 * Initializes the {@link TCheckboxWidget} "Show all stats" filter that shows
	 * statistics whose values are "0".
	 * @param context The {@link StatsView.FiltersInitContext}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public static final void initShowAllStatsFilter(final @NotNull StatsView.FiltersInitContext context) throws NullPointerException {
		initBooleanFilter(context, FID_EMPTYSTATS, false, BLanguage.gui_statsview_filter_showAllStats());
	}
	// ==================================================
	/**
	 * Initializes a {@link TCheckboxWidget} for a given {@code boolean} type filter.
	 * @param context The {@link StatsView.FiltersInitContext}.
	 * @param filterId The unique {@link Identifier} for the filter.
	 * @param defaultValue The filter's default value.
	 * @param labelText The checkbox label text.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public static final void initBooleanFilter(
			final @NotNull StatsView.FiltersInitContext context,
			final @NotNull Identifier filterId,
			final boolean defaultValue,
			final @NotNull Component labelText) throws NullPointerException
	{
		//preparation and math
		Objects.requireNonNull(context);
		Objects.requireNonNull(filterId);
		Objects.requireNonNull(labelText);
		final var panel    = context.getPanel();
		final var nextRect = panel.computeNextYBounds(20, GAP);

		//create and add the checkbox
		final var checkbox = new TCheckboxWidget();
		checkbox.setBounds(nextRect.x, nextRect.y, 20, nextRect.height);
		panel.add(checkbox);

		//create and add the label
		final var label    = new TLabelElement();
		label.setBounds(nextRect.x + 25, nextRect.y, nextRect.width - 25, nextRect.height);
		label.setText(labelText);
		panel.add(label);

		//set up initial value and change listeners
		checkbox.checkedProperty().set(
				context.getFilters().getProperty(Boolean.class, filterId, defaultValue),
				StatsViewUtils.class);
		checkbox.checkedProperty().addChangeListener((_, _, n) ->
				context.getFilters().setProperty(Boolean.class, filterId, n));
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
			@Nullable Component groupLabel,
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
			@Nullable Component groupLabel,
			@NotNull Iterable<CustomStat> customStats,
			@Nullable Consumer<TCustomStatWidget> widgetPostProcessor) throws NullPointerException
	{
		//not null requirements
		Objects.requireNonNull(panel);
		Objects.requireNonNull(customStats);

		//obtain iterator instance, cancel if there are no entries
		final var iterator = customStats.iterator();
		if(!iterator.hasNext()) return;

		//initialize group label
		if(groupLabel != null) initGroupLabel(panel, groupLabel);

		//initialize stats
		var nextBounds = panel.computeNextYBounds(18, GAP);
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
			@Nullable Component groupLabel,
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
			@Nullable Component groupLabel,
			@NotNull Iterable<ItemStats> itemStats,
			@Nullable Consumer<TItemStatsWidget> widgetPostProcessor) throws NullPointerException
	{
		//not null requirements
		Objects.requireNonNull(panel);
		Objects.requireNonNull(itemStats);

		//obtain iterator instance, cancel if there are no entries
		final var iterator = itemStats.iterator();
		if(!iterator.hasNext()) return;

		//initialize group label
		if(groupLabel != null) initGroupLabel(panel, groupLabel);

		//prepare maths to initialize stats
		int size = 20; //widget width and height
		int startX, endX, nextX, nextY; {
			final var nextBounds = panel.computeNextYBounds(0, GAP);
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
	 * Initializes a collection of "Block" statistics in a given "group".
	 * @param panel The {@link TPanelElement} where the stats initialize.
	 * @param groupLabel The display name of the group of statistics.
	 * @param blockStats The "Block" statistics collection.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 */
	public static final void initBlockStats(
			@NotNull TPanelElement panel,
			@Nullable Component groupLabel,
			@NotNull Iterable<BlockStats> blockStats) throws NullPointerException {
		initBlockStats(panel, groupLabel, blockStats, null);
	}

	/**
	 * Initializes a collection of "Block" statistics in a given "group".
	 * @param panel The {@link TPanelElement} where the stats initialize.
	 * @param groupLabel The display name of the group of statistics.
	 * @param blockStats The "Block" statistics collection.
	 * @param widgetPostProcessor An optional post-processor for each created {@link TBlockStatsWidget}.
	 * @throws NullPointerException If a {@link NotNull} argument is {@code null}.
	 */
	public static final void initBlockStats(
			@NotNull TPanelElement panel,
			@Nullable Component groupLabel,
			@NotNull Iterable<BlockStats> blockStats,
			@Nullable Consumer<TBlockStatsWidget> widgetPostProcessor) throws NullPointerException
	{
		//not null requirements
		Objects.requireNonNull(panel);
		Objects.requireNonNull(blockStats);

		//obtain iterator instance, cancel if there are no entries
		final var iterator = blockStats.iterator();
		if(!iterator.hasNext()) return;

		//initialize group label
		if(groupLabel != null) initGroupLabel(panel, groupLabel);

		//prepare maths to initialize stats
		int size = 20; //widget width and height
		int startX, endX, nextX, nextY; {
			final var nextBounds = panel.computeNextYBounds(0, GAP);
			startX = nextX = nextBounds.x;
			endX   = nextBounds.endX;
			nextY  = nextBounds.y;
		}

		//initialize stat widgets
		while(iterator.hasNext())
		{
			//create and add the widget
			final var widget = new TBlockStatsWidget(iterator.next());
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
			@Nullable Component groupLabel,
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
			@Nullable Component groupLabel,
			@NotNull Iterable<EntityStats> mobStats,
			@Nullable Consumer<TEntityStatsWidget> widgetPostProcessor) throws NullPointerException
	{
		//not null requirements
		Objects.requireNonNull(panel);
		Objects.requireNonNull(mobStats);

		//obtain iterator instance, cancel if there are no entries
		final var iterator = mobStats.iterator();
		if(!iterator.hasNext()) return;

		//initialize group label
		if(groupLabel != null) initGroupLabel(panel, groupLabel);

		//prepare maths to initialize stats
		int size = 30; //widget width and height
		int startX, endX, nextX, nextY; {
			final var nextBounds = panel.computeNextYBounds(0, GAP);
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
