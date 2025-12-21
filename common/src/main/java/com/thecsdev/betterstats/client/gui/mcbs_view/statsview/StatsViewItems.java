package com.thecsdev.betterstats.client.gui.mcbs_view.statsview;

import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils;
import com.thecsdev.betterstats.client.gui.panel.StatsPageChooser;
import com.thecsdev.betterstats.client.gui.panel.StatsSummaryPanel;
import com.thecsdev.betterstats.resources.BSSLang;
import com.thecsdev.betterstats.resources.BSSSprites;
import com.thecsdev.common.util.annotations.Virtual;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.ctxmenu.TContextMenu;
import com.thecsdev.commonmc.api.client.gui.misc.TTextureElement;
import com.thecsdev.commonmc.api.client.gui.tooltip.TTooltip;
import com.thecsdev.commonmc.api.client.gui.widget.TDropdownWidget;
import com.thecsdev.commonmc.api.client.gui.widget.stats.TItemStatsWidget;
import com.thecsdev.commonmc.api.stats.IStatsProvider;
import com.thecsdev.commonmc.api.stats.util.ItemStats;
import com.thecsdev.commonmc.api.util.modinfo.ModInfoProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.CreativeModeTab;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.thecsdev.betterstats.BetterStats.MOD_ID;
import static com.thecsdev.commonmc.api.world.item.TItemUtils.getCreativeModeTab;
import static com.thecsdev.commonmc.resources.TComponent.gui;
import static com.thecsdev.commonmc.resources.TComponent.item;
import static java.util.Comparator.comparing;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.resources.Identifier.DEFAULT_NAMESPACE;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

/**
 * {@link StatsView} that displays "Item" statistics.
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public sealed class StatsViewItems extends SubjectStatsView<ItemStats> permits StatsViewFood
{
	// ================================================== ==================================================
	//                                     StatsViewItems IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * The main instance of this {@link Class}.
	 */
	public static final StatsViewItems INSTANCE = new StatsViewItems();
	// ==================================================
	protected StatsViewItems() {}
	// ==================================================
	public @Override @NotNull Component getDisplayName() {
		return item("item/compass_12").append(" ").append(translatable("stat.itemsButton"));
	}
	// ==================================================
	public final @Override void initFilters(@NotNull FiltersInitContext context) {
		super.initFilters(context);
		SortBy.initFilter(context);
		GroupBy.initFilter(context);
	}
	// --------------------------------------------------
	public @Override void initStats(@NotNull StatsInitContext context)
	{
		//obtain stats
		final var allStats = ItemStats.getItemStats(
				context.getStatsReadOnly(),
				getStatsPredicate(context.getFilters()),
				getStatsSorter(context.getFilters()));
		if(allStats.isEmpty()) return; //nothing to show
		final int perPage    = 1550;
		final var pagedStats = StatsPageChooser.applyFilter(allStats, context.getFilters(), perPage);

		//group stats and initialize each group
		StatsPageChooser.initPanel(context.getPanel(), context.getFilters(), perPage, allStats.size());
		getStatsGrouper(context.getFilters())
				.apply(pagedStats).forEach((gName, gStats) ->
						StatsViewUtils.initItemStats(
								context.getPanel(), gName, gStats,
								widget -> postProcessWidget(context, widget)
						));
		StatsPageChooser.initPanel(context.getPanel(), context.getFilters(), perPage, allStats.size());
		StatsSummaryPanel.initPanel(context.getPanel(), allStats);
	}
	// ==================================================
	/**
	 * Post-processes a newly created {@link TItemStatsWidget}.
	 * @param context The {@link StatsView.StatsInitContext}.
	 * @param widget The newly created {@link TItemStatsWidget}.
	 * @throws NullPointerException If any of the arguments is {@code null}.
	 */
	protected @Virtual void postProcessWidget(@NotNull StatsView.StatsInitContext context, @NotNull TItemStatsWidget widget) {
		//obtain stats instance
		final var stats = widget.statsProperty().get();
		assert stats != null;
		//noinspection unchecked - context menu
		widget.contextMenuProperty().set((Function<TElement, TContextMenu>)(Object) CTX_MENU, StatsViewItems.class);
	}

	/**
	 * Constructs a context menu for a given {@link TItemStatsWidget}.
	 */
	private static final Function<TItemStatsWidget, TContextMenu> CTX_MENU = widget ->
	{
		//obtain the stats data and ensure it is present
		final var stats = widget.statsProperty().get();
		assert stats != null;

		//create the context menu builder
		final var builder = new TContextMenu.Builder(Objects.requireNonNull(widget.getClient()));

		//wiki url
		if(Objects.equals(stats.getSubjectID().getNamespace(), DEFAULT_NAMESPACE)) {
			final var url_wiki = String.format("https://minecraft.wiki/w/%s", stats.getSubjectID().getPath());
			builder.addButton(
					gui(BSSSprites.gui_icon_faviconWiki()).append(" ").append(BSSLang.gui_statsview_stats_ctxMenu_viewOnWiki()),
					__ -> Util.getPlatform().openUri(url_wiki));
		}

		//close button, and thenbuild and return a new context menu
		builder.addButton(
			gui(BSSSprites.gui_icon_close()).append(" ").append(BSSLang.gui_menubar_file_close()),
			__ -> {});
		return builder.build();
	};
	// --------------------------------------------------
	protected @Virtual @Override @NotNull Comparator<ItemStats> getStatsSorter(@NotNull Filters filters) throws NullPointerException {
		return filters.getProperty(SortBy.class, SortBy.FID, SortBy.VANILLA).getStatsSorter();
	}

	protected final @Override @NotNull Function<Iterable<ItemStats>, LinkedHashMap<Component, Iterable<ItemStats>>> getStatsGrouper(@NotNull Filters filters) {
		return filters.getProperty(GroupBy.class, GroupBy.FID, GroupBy.TAB).getStatsGrouper();
	}
	// ================================================== ==================================================
	//                                             SortBy IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link TDropdownWidget.Entry}s for sorting {@link ItemStats}.
	 */
	public static final class SortBy implements TDropdownWidget.Entry
	{
		// ==================================================
		public static final SortBy VANILLA = new SortBy(
				"VANILLA", Component.literal("-"),
				(o1, o2) -> 0);
		public static final SortBy ALPHABETICAL = new SortBy(
				"ALPHABETICAL", Component.literal("A-Z"),
				comparing(stat -> stat.getSubjectDisplayName().getString()));
		public static final SortBy LACITEBAHPLA = new SortBy(
				"LACITEBAHPLA", Component.literal("Z-A"),
				ALPHABETICAL.sorter.reversed());
		// --------------------------------------------------
		/**
		 * The main "filter id" used for {@link StatsView.Filters}.
		 */
		public static final Identifier FID = fromNamespaceAndPath(MOD_ID, "items_sort_by");
		// --------------------------------------------------
		private final @NotNull Object handle;
		private final @NotNull Component name;
		private final @NotNull Comparator<ItemStats> sorter;
		// ==================================================
		private SortBy(
				@NotNull Object handle,
				@NotNull Component name,
				@NotNull Comparator<ItemStats> sorter) throws NullPointerException {
			this.handle = Objects.requireNonNull(handle);
			this.name   = Objects.requireNonNull(name);
			this.sorter = Objects.requireNonNull(sorter);
		}
		// ==================================================
		public final @Override @NotNull Component getDisplayName() { return this.name; }
		public @NotNull Comparator<ItemStats> getStatsSorter() { return this.sorter; }
		// ==================================================
		public final @Override int hashCode() { return Objects.hash(this.handle); }
		public final @Override boolean equals(Object obj) {
			if(obj == this) return true;
			if(!(obj instanceof SortBy other)) return false;
			return Objects.equals(this.handle, other.handle);
		}
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
			dropdown.getEntries().addAll(values());
			dropdown.tooltipProperty().set(__ -> TTooltip.of(BSSLang.gui_statsview_filter_sortBy()), SortBy.class);
			panel.add(dropdown);

			//set initial value and apply filters on value update
			dropdown.selectedEntryProperty().set(
					context.getFilters().getProperty(SortBy.class, FID, VANILLA),
					SortBy.class);
			dropdown.selectedEntryProperty().addChangeListener((p, o, n) ->
					context.getFilters().setProperty(SortBy.class, FID, n));
		}
		// ==================================================
		/**
		 * Retuns all {@link SortBy} instances that are to be used.
		 */
		public static final @NotNull Collection<SortBy> values()
		{
			//initialize the list
			final var ist  = IStatsProvider.getItemStatTypes();
			final var list = new ArrayList<SortBy>(2 + ist.size());
			//add sorting entries
			list.add(VANILLA);
			list.add(ALPHABETICAL);
			list.add(LACITEBAHPLA);
			for(final var statType : ist)
				list.add(new SortBy(
						statType, IStatsProvider.getStatTypeName(statType),
						comparing(s -> s.getStatsProvider().getIntValue(statType, s.getSubject()))
				));
			//return the result
			return list;
		}
		// ==================================================
	}
	// ================================================== ==================================================
	//                                            GroupBy IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link TDropdownWidget.Entry}s for grouping {@link ItemStats}.
	 */
	public static enum GroupBy implements TDropdownWidget.Entry
	{
		// ==================================================
		ALL(BSSLang.gui_statsview_filter_groupBy_all(), stats -> {
			final var map = new LinkedHashMap<Component, Iterable<ItemStats>>();
			map.put(Component.literal("*"), stats);
			return map;
		}),
		MOD(BSSLang.gui_statsview_filter_groupBy_mod(), stats -> {
			//create a new map to group stats based on mod id-s
			final var map = new LinkedHashMap<String, ArrayList<ItemStats>>();
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
		}),
		TAB(BSSLang.gui_statsview_filter_groupBy_createiveModeTab(), stats -> {
			//create a new map to group stats based on creative mode tabs
			final var map = new LinkedHashMap<CreativeModeTab, ArrayList<ItemStats>>();
			//group the stats
			for(final var stat : stats)
				map.computeIfAbsent(getCreativeModeTab(stat.getSubject()), __ -> new ArrayList<>())
						.add(stat);
			//remap the map and return it
			return map.entrySet().stream().collect(Collectors.toMap(
					entry -> entry.getKey() != null ? entry.getKey().getDisplayName() : Component.literal("*"),
					Map.Entry::getValue,
					(existing, replacement) -> existing,
					LinkedHashMap::new
			));
		});
		// --------------------------------------------------
		/**
		 * The main "filter id" used for {@link StatsView.Filters}.
		 */
		public static final Identifier FID = fromNamespaceAndPath(MOD_ID, "items_group_by");
		// --------------------------------------------------
		private final @NotNull Component name;
		private final @NotNull Function<Iterable<ItemStats>, LinkedHashMap<Component, Iterable<ItemStats>>> grouper;
		// ==================================================
		GroupBy(@NotNull Component name,
				@NotNull Function<Iterable<ItemStats>, LinkedHashMap<Component, Iterable<ItemStats>>> grouper) {
			this.name    = Objects.requireNonNull(name);
			this.grouper = grouper;
		}
		// ==================================================
		public final @Override @NotNull Component getDisplayName() { return this.name; }
		public final @NotNull Function<Iterable<ItemStats>, LinkedHashMap<Component, Iterable<ItemStats>>> getStatsGrouper() { return this.grouper; }
		// ==================================================
		/**
		 * Initializes GUI for the {@link StatsViewItems.GroupBy} filter.
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

			final var dropdown = new TDropdownWidget<StatsViewItems.GroupBy>();
			dropdown.setBounds(nextY.x + 25, nextY.y, nextY.width - 25, nextY.height);
			Collections.addAll(dropdown.getEntries(), StatsViewItems.GroupBy.values());
			dropdown.tooltipProperty().set(__ -> TTooltip.of(BSSLang.gui_statsview_filter_groupBy()), GroupBy.class);
			panel.add(dropdown);

			//set initial value and apply filters on value update
			dropdown.selectedEntryProperty().set(
					context.getFilters().getProperty(StatsViewItems.GroupBy.class, FID, TAB),
					StatsViewItems.GroupBy.class);
			dropdown.selectedEntryProperty().addChangeListener((p, o, n) ->
					context.getFilters().setProperty(GroupBy.class, FID, n));
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
