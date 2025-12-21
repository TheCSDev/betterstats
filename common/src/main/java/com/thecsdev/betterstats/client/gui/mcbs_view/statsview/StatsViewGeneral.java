package com.thecsdev.betterstats.client.gui.mcbs_view.statsview;

import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils;
import com.thecsdev.betterstats.client.gui.panel.StatsPageChooser;
import com.thecsdev.betterstats.resources.BSSLang;
import com.thecsdev.betterstats.resources.BSSSprites;
import com.thecsdev.commonmc.api.client.gui.misc.TTextureElement;
import com.thecsdev.commonmc.api.client.gui.tooltip.TTooltip;
import com.thecsdev.commonmc.api.client.gui.widget.TDropdownWidget;
import com.thecsdev.commonmc.api.client.gui.widget.stats.TCustomStatWidget;
import com.thecsdev.commonmc.api.stats.util.CustomStat;
import com.thecsdev.commonmc.api.stats.util.StatFormatterOverride;
import com.thecsdev.commonmc.api.util.modinfo.ModInfoProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.thecsdev.betterstats.BetterStats.MOD_ID;
import static com.thecsdev.commonmc.resources.TComponent.item;
import static java.util.Comparator.comparing;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

/**
 * {@link StatsView} that displays "General" statistics.
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class StatsViewGeneral extends SubjectStatsView<CustomStat>
{
	// ================================================== ==================================================
	//                                   StatsViewGeneral IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * The main instance of this {@link Class}.
	 */
	public static final StatsViewGeneral INSTANCE = new StatsViewGeneral();
	// ==================================================
	private StatsViewGeneral() {}
	// ==================================================
	public final @Override @NotNull Component getDisplayName() {
		return item("item/paper").append(" ").append(translatable("stat.generalButton"));
	}
	// ==================================================
	public final @Override void initFilters(@NotNull FiltersInitContext context) {
		super.initFilters(context);
		SortBy.initFilter(context);
		GroupBy.initFilter(context);
		DistanceUnit.initFilter(context);
		TimeUnit.initFilter(context);
	}
	// --------------------------------------------------
	public final @Override void initStats(@NotNull StatsInitContext context)
	{
		//obtain stats
		final var allStats = CustomStat.getCustomStats(
				context.getStatsReadOnly(),
				getStatsPredicate(context.getFilters()),
				getStatsSorter(context.getFilters()));
		if(allStats.isEmpty()) return; //nothing to show
		final int perPage    = 200;
		final var pagedStats = StatsPageChooser.applyFilter(allStats, context.getFilters(), perPage);

		//group stats and initialize each group
		StatsPageChooser.initPanel(context.getPanel(), context.getFilters(), perPage, allStats.size());
		getStatsGrouper(context.getFilters())
				.apply(pagedStats).forEach((gName, gStats) ->
						StatsViewUtils.initGeneralStats(
								context.getPanel(), gName, gStats,
								el -> postProcessWidget(context, el)
						));
		StatsPageChooser.initPanel(context.getPanel(), context.getFilters(), perPage, allStats.size());
	}
	// ==================================================
	/**
	 * Post-processes a {@link TCustomStatWidget} after its creation.
	 * @param context The {@link StatsView.StatsInitContext}.
	 * @param widget The {@link TCustomStatWidget} to post-process.
	 * @throws NullPointerException If any of the arguments is {@code null}.
	 */
	private final void postProcessWidget(@NotNull StatsView.StatsInitContext context, @NotNull TCustomStatWidget widget)
	{
		//obtain stat instance
		final var stat = widget.statProperty().get();
		assert stat != null;
		//stat formatter overrides
		if(stat.isDistance()) {
			final var distanceUnit = context.getFilters().getProperty(DistanceUnit.class, DistanceUnit.FID, DistanceUnit.VANILLA);
			widget.formatterOverrideProperty().set(distanceUnit.getFormatter(), StatsViewGeneral.class);
		}
		else if(stat.isTime()) {
			final var timeUnit = context.getFilters().getProperty(TimeUnit.class, TimeUnit.FID, TimeUnit.VANILLA);
			widget.formatterOverrideProperty().set(timeUnit.getFormatter(), StatsViewGeneral.class);
		}
	}
	// --------------------------------------------------
	protected final @Override @NotNull Comparator<CustomStat> getStatsSorter(@NotNull Filters filters) throws NullPointerException {
		return filters.getProperty(SortBy.class, SortBy.FID, SortBy.ALPHABETICAL).getStatsSorter();
	}

	protected final @Override @NotNull Function<Iterable<CustomStat>, LinkedHashMap<Component, Iterable<CustomStat>>> getStatsGrouper(@NotNull Filters filters) {
		return filters.getProperty(GroupBy.class, GroupBy.FID, GroupBy.MOD).getStatsGrouper();
	}
	// ================================================== ==================================================
	//                                             SortBy IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link TDropdownWidget.Entry}s for sorting {@link CustomStat}.
	 */
	public static enum SortBy implements TDropdownWidget.Entry
	{
		// ==================================================
		VANILLA      (literal("-"),   (o1, o2) -> 0), //order in which stats were registered
		ALPHABETICAL (literal("A-Z"), comparing(stat -> stat.getSubjectDisplayName().getString())),
		LACITEBAHPLA (literal("Z-A"), ALPHABETICAL.getStatsSorter().reversed()),
		INCREMENTAL  (literal("0-9"), comparing(CustomStat::getValue)),
		DECREMENTAL  (literal("9-0"), INCREMENTAL.getStatsSorter().reversed());
		// --------------------------------------------------
		/**
		 * The main "filter id" used for {@link StatsView.Filters}.
		 */
		public static final Identifier FID = fromNamespaceAndPath(MOD_ID, "general_sort_by");
		// --------------------------------------------------
		private final @NotNull Component                name;
		private final @NotNull Comparator<CustomStat> sorter;
		// ==================================================
		SortBy(@NotNull Component name, @NotNull Comparator<CustomStat> sorter) {
			this.name   = Objects.requireNonNull(name);
			this.sorter = sorter;
		}
		// ==================================================
		public final @Override @NotNull Component getDisplayName() { return this.name; }

		/**
		 * Returns the statistics sorder associated with this {@link SortBy} instance.
		 * @see SubjectStatsView#getStatsSorter(Filters)
		 */
		public final @NotNull Comparator<CustomStat> getStatsSorter() { return this.sorter; }
		// ==================================================
		/**
		 * Initializes GUI for the {@link SortBy} filter.
		 * @param context The {@link StatsView.FiltersInitContext}.
		 * @throws NullPointerException If the argument is {@code null}.
		 */
		public static final void initFilter(@NotNull StatsView.FiltersInitContext context) throws NullPointerException
		{
			//create and add the icon and widget
			final var panel = context.getPanel();
			final var nextY = panel.computeNextYBounds(20, StatsViewUtils.GAP);

			final var icon = new TTextureElement(BSSSprites.gui_icon_filterSort());
			icon.setBounds(nextY.x, nextY.y, 20, nextY.height);
			panel.add(icon);

			final var dropdown = new TDropdownWidget<SortBy>();
			dropdown.setBounds(nextY.x + 25, nextY.y, nextY.width - 25, nextY.height);
			Collections.addAll(dropdown.getEntries(), SortBy.values());
			dropdown.tooltipProperty().set(__ -> TTooltip.of(BSSLang.gui_statsview_filter_sortBy()), SortBy.class);
			panel.add(dropdown);

			//set initial value and apply filters on value update
			dropdown.selectedEntryProperty().set(
					context.getFilters().getProperty(SortBy.class, FID, ALPHABETICAL),
					SortBy.class);
			dropdown.selectedEntryProperty().addChangeListener((p, o, n) ->
					context.getFilters().setProperty(SortBy.class, FID, n));
		}
		// ==================================================
	}
	// ================================================== ==================================================
	//                                            GroupBy IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link TDropdownWidget.Entry}s for grouping {@link CustomStat}.
	 */
	public static enum GroupBy implements TDropdownWidget.Entry
	{
		// ==================================================
		ALL(BSSLang.gui_statsview_filter_groupBy_all(), stats -> {
			final var map = new LinkedHashMap<Component, Iterable<CustomStat>>();
			map.put(literal("*"), stats);
			return map;
		}),
		MOD(BSSLang.gui_statsview_filter_groupBy_mod(), stats -> {
			//create a new map to group stats based on mod id-s
			final var map = new LinkedHashMap<String, ArrayList<CustomStat>>();
			//group the stats
			for(final var stat : stats)
				map.computeIfAbsent(stat.getSubjectID().getNamespace(), __ -> new ArrayList<>())
						.add(stat);
			//remap the map and return it
			return map.entrySet().stream().collect(Collectors.toMap(
					entry -> {
						assert ModInfoProvider.getInstance() != null;
						return ModInfoProvider.getInstance().getModInfo(entry.getKey()).getName();
					},
					Map.Entry::getValue,
					(existing, replacement) -> existing,
					LinkedHashMap::new
			));
		});
		// --------------------------------------------------
		/**
		 * The main "filter id" used for {@link StatsView.Filters}.
		 */
		public static final Identifier FID = fromNamespaceAndPath(MOD_ID, "general_group_by");
		// --------------------------------------------------
		private final @NotNull Component name;
		private final @NotNull Function<Iterable<CustomStat>, LinkedHashMap<Component, Iterable<CustomStat>>> grouper;
		// ==================================================
		GroupBy(@NotNull Component name,
				@NotNull Function<Iterable<CustomStat>, LinkedHashMap<Component, Iterable<CustomStat>>> grouper) {
			this.name    = Objects.requireNonNull(name);
			this.grouper = grouper;
		}
		// ==================================================
		public final @Override @NotNull Component getDisplayName() { return this.name; }
		public final @NotNull Function<Iterable<CustomStat>, LinkedHashMap<Component, Iterable<CustomStat>>> getStatsGrouper() { return this.grouper; }
		// ==================================================
		/**
		 * Initializes GUI for the {@link GroupBy} filter.
		 * @param context The {@link StatsView.FiltersInitContext}.
		 * @throws NullPointerException If the argument is {@code null}.
		 */
		public static final void initFilter(@NotNull StatsView.FiltersInitContext context) throws NullPointerException
		{
			//create and add the icon and widget
			final var panel = context.getPanel();
			final var nextY = panel.computeNextYBounds(20, StatsViewUtils.GAP);

			final var icon = new TTextureElement(BSSSprites.gui_icon_filterGroup());
			icon.setBounds(nextY.x, nextY.y, 20, nextY.height);
			panel.add(icon);

			final var dropdown = new TDropdownWidget<GroupBy>();
			dropdown.setBounds(nextY.x + 25, nextY.y, nextY.width - 25, nextY.height);
			Collections.addAll(dropdown.getEntries(), GroupBy.values());
			dropdown.tooltipProperty().set(__ -> TTooltip.of(BSSLang.gui_statsview_filter_groupBy()), GroupBy.class);
			panel.add(dropdown);

			//set initial value and apply filters on value update
			dropdown.selectedEntryProperty().set(
					context.getFilters().getProperty(GroupBy.class, FID, MOD),
					GroupBy.class);
			dropdown.selectedEntryProperty().addChangeListener((p, o, n) ->
					context.getFilters().setProperty(GroupBy.class, FID, n));
		}
		// ==================================================
	}
	// ================================================== ==================================================
	//                                       DistanceUnit IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link TDropdownWidget.Entry}s for overriding distance stat formatters.
	 */
	public static enum DistanceUnit implements TDropdownWidget.Entry
	{
		// ==================================================
		VANILLA   (literal("-"),  StatFormatterOverride.DEFAULT),
		CENTIMETER(literal("cm"), (f, v) -> (f != StatFormatter.DISTANCE) ? f.format(v) : String.format("%d cm", v)),              //v is in centimeters
		METER     (literal("m"),  (f, v) -> (f != StatFormatter.DISTANCE) ? f.format(v) : String.format("%.2f m", v / 100.0)),     //v is in centimeters
		KILOMETER (literal("km"), (f, v) -> (f != StatFormatter.DISTANCE) ? f.format(v) : String.format("%.3f km", v / 100000.0)), //v is in centimeters
		INCH      (literal("in"), (f, v) -> (f != StatFormatter.DISTANCE) ? f.format(v) : String.format("%.2f in", v / 2.54)),     //v is in centimeters
		FOOT      (literal("ft"), (f, v) -> (f != StatFormatter.DISTANCE) ? f.format(v) : String.format("%.2f ft", v / 30.48)),    //v is in centimeters
		YARD      (literal("yd"), (f, v) -> (f != StatFormatter.DISTANCE) ? f.format(v) : String.format("%.2f yd", v / 91.44)),    //v is in centimeters
		MILE      (literal("mi"), (f, v) -> (f != StatFormatter.DISTANCE) ? f.format(v) : String.format("%.3f mi", v / 160934.4)); //v is in centimeters
		// --------------------------------------------------
		/**
		 * The main "filter id" used for {@link StatsView.Filters}.
		 */
		public static final Identifier FID = fromNamespaceAndPath(MOD_ID, "distance_unit");
		// ==================================================
		private final @NotNull Component             name;
		private final @NotNull StatFormatterOverride formatter;
		// ==================================================
		DistanceUnit(@NotNull Component name, @NotNull StatFormatterOverride formatter) {
			this.name      = Objects.requireNonNull(name);
			this.formatter = Objects.requireNonNull(formatter);
		}
		// ==================================================
		public final @Override @NotNull Component getDisplayName() { return this.name; }
		public final @NotNull StatFormatterOverride getFormatter() { return this.formatter; }
		// ==================================================
		/**
		 * Initializes GUI for the {@link DistanceUnit} filter.
		 * @param context The {@link StatsView.FiltersInitContext}.
		 * @throws NullPointerException If the argument is {@code null}.
		 */
		public static final void initFilter(@NotNull StatsView.FiltersInitContext context) throws NullPointerException
		{
			//create and add the icon and widget
			final var panel = context.getPanel();
			final var nextY = panel.computeNextYBounds(20, StatsViewUtils.GAP);

			final var icon = new TTextureElement(BSSSprites.gui_icon_filterUnitDist());
			icon.setBounds(nextY.x, nextY.y, 20, nextY.height);
			panel.add(icon);

			final var dropdown = new TDropdownWidget<DistanceUnit>();
			dropdown.setBounds(nextY.x + 25, nextY.y, nextY.width - 25, nextY.height);
			Collections.addAll(dropdown.getEntries(), DistanceUnit.values());
			dropdown.tooltipProperty().set(__ -> TTooltip.of(BSSLang.gui_statsview_filter_distanceUnit()), DistanceUnit.class);
			panel.add(dropdown);

			//set initial value and apply filters on value update
			dropdown.selectedEntryProperty().set(
					context.getFilters().getProperty(DistanceUnit.class, FID, DistanceUnit.VANILLA),
					DistanceUnit.class);
			dropdown.selectedEntryProperty().addChangeListener((p, o, n) ->
					context.getFilters().setProperty(DistanceUnit.class, FID, n));
		}
		// ==================================================
	}
	// ================================================== ==================================================
	//                                           TimeUnit IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link TDropdownWidget.Entry}s for overriding time stat formatters.
	 */
	public static enum TimeUnit implements TDropdownWidget.Entry
	{
		// ==================================================
		VANILLA     (literal("-"),  StatFormatterOverride.DEFAULT),
		MILLISECOND (literal("ms"),    (f, v) -> (f != StatFormatter.TIME) ? f.format(v) : String.format("%.2f ms", v / 0.02)),       //v is in ticks
		TICK        (literal("0.05s"), (f, v) -> (f != StatFormatter.TIME) ? f.format(v) : String.format("%d t", v)),                 //v is in ticks
		SECOND      (literal("s"),     (f, v) -> (f != StatFormatter.TIME) ? f.format(v) : String.format("%.2f s", v / 20.0)),        //v is in ticks
		MINUTE      (literal("m"),     (f, v) -> (f != StatFormatter.TIME) ? f.format(v) : String.format("%.2f m", v / 1200.0)),      //v is in ticks
		HOUR        (literal("h"),     (f, v) -> (f != StatFormatter.TIME) ? f.format(v) : String.format("%.2f h", v / 72000.0)),     //v is in ticks
		DAY         (literal("d"),     (f, v) -> (f != StatFormatter.TIME) ? f.format(v) : String.format("%.2f d", v / 1728000.0)),   //v is in ticks
		WEEK        (literal("w"),     (f, v) -> (f != StatFormatter.TIME) ? f.format(v) : String.format("%.3f w", v / 12096000.0)),  //v is in ticks
		MONTH       (literal("mo"),    (f, v) -> (f != StatFormatter.TIME) ? f.format(v) : String.format("%.3f mo", v / 51840000.0)), //v is in ticks
		YEAR        (literal("y"),     (f, v) -> (f != StatFormatter.TIME) ? f.format(v) : String.format("%.4f y", v / 622080000.0)), //v is in ticks
		HHMMSSMS    (literal("hh:mm:ss.ms"), (f, v) -> { //v is in ticks
			if(f != StatFormatter.TIME) return f.format(v);
			final var totalMilliseconds = v * 50;
			final var hours   = totalMilliseconds / 3600000;
			final var minutes = (totalMilliseconds % 3600000) / 60000;
			final var seconds = (totalMilliseconds % 60000) / 1000;
			final var millis  = totalMilliseconds % 1000;
			return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
		});
		// --------------------------------------------------
		/**
		 * The main "filter id" used for {@link StatsView.Filters}.
		 */
		public static final Identifier FID = fromNamespaceAndPath(MOD_ID, "time_unit");
		// ==================================================
		private final @NotNull Component             name;
		private final @NotNull StatFormatterOverride formatter;
		// ==================================================
		TimeUnit(@NotNull Component name, @NotNull StatFormatterOverride formatter) {
			this.name      = Objects.requireNonNull(name);
			this.formatter = Objects.requireNonNull(formatter);
		}
		// =================================================
		public final @Override @NotNull Component getDisplayName() { return this.name; }
		public final @NotNull StatFormatterOverride getFormatter() { return this.formatter; }
		// ==================================================
		/**
		 * Initializes GUI for the {@link TimeUnit} filter.
		 * @param context The {@link StatsView.FiltersInitContext}.
		 * @throws NullPointerException If the argument is {@code null}.
		 */
		public static final void initFilter(@NotNull StatsView.FiltersInitContext context) throws NullPointerException
		{
			//create and add the icon and widget
			final var panel = context.getPanel();
			final var nextY = panel.computeNextYBounds(20, StatsViewUtils.GAP);

			final var icon = new TTextureElement(BSSSprites.gui_icon_filterUnitTime());
			icon.setBounds(nextY.x, nextY.y, 20, nextY.height);
			panel.add(icon);

			final var dropdown = new TDropdownWidget<TimeUnit>();
			dropdown.setBounds(nextY.x + 25, nextY.y, nextY.width - 25, nextY.height);
			Collections.addAll(dropdown.getEntries(), TimeUnit.values());
			dropdown.tooltipProperty().set(__ -> TTooltip.of(BSSLang.gui_statsview_filter_timeUnit()), TimeUnit.class);
			panel.add(dropdown);

			//set initial value and apply filters on value update
			dropdown.selectedEntryProperty().set(
					context.getFilters().getProperty(TimeUnit.class, FID, TimeUnit.VANILLA),
					TimeUnit.class);
			dropdown.selectedEntryProperty().addChangeListener((p, o, n) ->
					context.getFilters().setProperty(TimeUnit.class, FID, n));
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
