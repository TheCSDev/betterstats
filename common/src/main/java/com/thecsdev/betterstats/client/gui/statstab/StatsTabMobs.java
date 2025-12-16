package com.thecsdev.betterstats.client.gui.statstab;

import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.api.client.gui.statstab.StatsTab;
import com.thecsdev.betterstats.api.client.gui.statstab.StatsTabUtils;
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
import com.thecsdev.commonmc.api.client.gui.widget.stats.TEntityStatsWidget;
import com.thecsdev.commonmc.api.stats.StatsProvider;
import com.thecsdev.commonmc.api.stats.util.EntityStats;
import com.thecsdev.commonmc.api.util.modinfo.ModInfoProvider;
import net.minecraft.util.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.MobCategory;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.thecsdev.betterstats.BetterStats.MOD_ID;
import static com.thecsdev.commonmc.resources.TComponent.gui;
import static com.thecsdev.commonmc.resources.TComponent.head;
import static java.util.Comparator.comparing;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.resources.Identifier.DEFAULT_NAMESPACE;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

/**
 * {@link StatsTab} that displays "Mobs" statistics.
 */
public sealed class StatsTabMobs extends BetterStatsTab<EntityStats> permits StatsTabHunter
{
	// ================================================== ==================================================
	//                                       StatsTabMobs IMPLEMENTATION
	// ================================================== ==================================================
	public static final StatsTabMobs INSTANCE = new StatsTabMobs();
	// ==================================================
	protected StatsTabMobs() {}
	// ==================================================
	public @Virtual @Override @NotNull Component getDisplayName() {
		return head("MHF_Pig").append(" ").append(translatable("stat.mobsButton"));
	}
	// --------------------------------------------------
	public final @Override void initFilters(FiltersInitContext context) {
		super.initFilters(context);
		SortBy.initFilter(context);
		GroupBy.initFilter(context);
	}
	// --------------------------------------------------
	public final @Override void initStats(StatsTab.StatsInitContext context)
	{
		//obtain stats
		final var allStats = EntityStats.getEntityStats(
				context.getStatsProvider(),
				getStatsPredicate(context.getFilters()),
				getStatsSorter(context.getFilters()));
		if(allStats.isEmpty()) return; //nothing to show
		final int perPage    = 200;
		final var pagedStats = StatsPageChooser.applyFilter(allStats, context.getFilters(), perPage);

		//group stats and initialize each group
		Optional.ofNullable(StatsPageChooser.initPanel(context.getPanel(), context.getFilters(), perPage, allStats.size()))
				.ifPresent(spc -> spc.ePageChanged.register(__ -> context.refresh()));
		getStatsGrouper(context.getFilters())
				.apply(pagedStats).forEach((gName, gStats) ->
						StatsTabUtils.initMobStats(
								context.getPanel(), gName, gStats,
								widget -> postProcessWidget(context, widget)
						));
		Optional.ofNullable(StatsPageChooser.initPanel(context.getPanel(), context.getFilters(), perPage, allStats.size()))
				.ifPresent(spc -> spc.ePageChanged.register(__ -> context.refresh()));
		StatsSummaryPanel.initPanel(context.getPanel(), allStats); //NOTE: Hmm, paged or all... What do I use...?
	}
	// ==================================================
	/**
	 * Post-processes each {@link TEntityStatsWidget} after its creation.
	 * @param context The {@link StatsTab.StatsInitContext}.
	 * @param widget The {@link TEntityStatsWidget} to post-process.
	 * @throws NullPointerException If any of the arguments is {@code null}.
	 */
	protected @Virtual void postProcessWidget(@NotNull StatsTab.StatsInitContext context, @NotNull TEntityStatsWidget widget)
	{
		//obtain stats instance
		final var stats = widget.statsProperty().get();
		assert stats != null;
		//noinspection unchecked - context menu
		widget.contextMenuProperty().set((Function<TElement, TContextMenu>)(Object) CTX_MENU, StatsTabMobs.class);
		widget.followsCursorProperty().set(BetterStats.getConfig().getGuiMobsFollowCursor(), StatsTabMobs.class);
	}

	/**
	 * Constructs a context menu for a given {@link TEntityStatsWidget}.
	 */
	private static final Function<TEntityStatsWidget, TContextMenu> CTX_MENU = widget ->
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
					gui(BSSSprites.gui_icon_faviconWiki()).append(" ").append(BSSLang.gui_statstab_stats_ctxMenu_viewOnWiki()),
					__ -> Util.getPlatform().openUri(url_wiki));
		}

		//close button, and thenbuild and return a new context menu
		builder.addButton(
			gui(BSSSprites.gui_icon_close()).append(" ").append(BSSLang.gui_menubar_file_close()),
			__ -> {});
		return builder.build();
	};
	// --------------------------------------------------
	protected @Virtual @Override @NotNull Comparator<EntityStats> getStatsSorter(@NotNull Filters filters) throws NullPointerException {
		return filters.getProperty(SortBy.class, SortBy.FID, SortBy.VANILLA).getStatsSorter();
	}

	protected final @Override @NotNull Function<Iterable<EntityStats>, LinkedHashMap<Component, Iterable<EntityStats>>> getStatsGrouper(@NotNull Filters filters) {
		return filters.getProperty(GroupBy.class, GroupBy.FID, GroupBy.MOD).getStatsGrouper();
	}
	// ================================================== ==================================================
	//                                             SortBy IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link TDropdownWidget.Entry}s for sorting {@link EntityStats}.
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
		 * The main "filter id" used for {@link StatsTab.Filters}.
		 */
		public static final Identifier FID = fromNamespaceAndPath(MOD_ID, "entities_sort_by");
		// --------------------------------------------------
		private final @NotNull Object handle;
		private final @NotNull Component name;
		private final @NotNull Comparator<EntityStats> sorter;
		// ==================================================
		private SortBy(
				@NotNull Object handle,
				@NotNull Component name,
				@NotNull Comparator<EntityStats> sorter) throws NullPointerException {
			this.handle = Objects.requireNonNull(handle);
			this.name   = Objects.requireNonNull(name);
			this.sorter = Objects.requireNonNull(sorter);
		}
		// ==================================================
		public final @Override @NotNull Component getDisplayName() { return this.name; }
		public @NotNull Comparator<EntityStats> getStatsSorter() { return this.sorter; }
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
		 * @param context The {@link StatsTab.FiltersInitContext}.
		 * @throws NullPointerException If the argument is {@code null}.
		 */
		public static final void initFilter(@NotNull StatsTab.FiltersInitContext context) throws NullPointerException
		{
			//create and add the icon and widget
			final var panel = context.getPanel();
			final var nextY = StatsTabUtils.nextYBounds(panel, 20);

			final var icon = new TTextureElement(BSSSprites.gui_icon_filterSort());
			icon.setBounds(nextY.x, nextY.y, 20, nextY.height);
			panel.add(icon);

			final var dropdown = new TDropdownWidget<SortBy>();
			dropdown.setBounds(nextY.x + 25, nextY.y, nextY.width - 25, nextY.height);
			dropdown.getEntries().addAll(values());
			dropdown.tooltipProperty().set(__ -> TTooltip.of(BSSLang.gui_statstab_filter_sortBy()), SortBy.class);
			panel.add(dropdown);

			//set initial value and apply filters on value update
			dropdown.selectedEntryProperty().set(
					context.getFilters().getProperty(SortBy.class, FID, VANILLA),
					SortBy.class);
			dropdown.selectedEntryProperty().addChangeListener((p, o, n) -> {
				context.getFilters().setProperty(SortBy.class, FID, n);
				context.applyFilters();
			});
		}
		// ==================================================
		/**
		 * Retuns all {@link SortBy} instances that are to be used.
		 */
		public static final @NotNull Collection<SortBy> values()
		{
			//initialize the list
			final var ist  = StatsProvider.getEntityStatTypes();
			final var list = new ArrayList<SortBy>(2 + ist.size());
			//add sorting entries
			list.add(VANILLA);
			list.add(ALPHABETICAL);
			list.add(LACITEBAHPLA);
			for(final var statType : ist)
				list.add(new SortBy(
						statType, StatsProvider.getStatTypeName(statType),
						comparing(s -> s.getStatsProvider().getValue(statType, s.getSubject()))
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
	 * {@link TDropdownWidget.Entry}s for grouping {@link EntityStats}.
	 */
	public static enum GroupBy implements TDropdownWidget.Entry
	{
		// ==================================================
		ALL(BSSLang.gui_statstab_filter_groupBy_all(), stats -> {
			final var map = new LinkedHashMap<Component, Iterable<EntityStats>>();
			map.put(Component.literal("*"), stats);
			return map;
		}),
		MOD(Component.literal("Mod"), stats -> {
			//create a new map to group stats based on mod id-s
			final var map = new LinkedHashMap<String, ArrayList<EntityStats>>();
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
		CATEGORY(BSSLang.gui_statstab_filter_groupBy_mobCategory(), stats -> {
			//create a new map to group stats based on mob categories
			final var map = new LinkedHashMap<MobCategory, ArrayList<EntityStats>>();
			//group the stats
			for(final var stat : stats) {
				final var category = stat.getSubject().getCategory();
				map.computeIfAbsent(category, __ -> new ArrayList<>()).add(stat);
			}
			//remap the map and return it
			return map.entrySet().stream().collect(Collectors.toMap(
					entry -> translatable("mobCategory." + entry.getKey().getName()),
					Map.Entry::getValue,
					(existing, replacement) -> existing,
					LinkedHashMap::new
			));
		});
		// --------------------------------------------------
		/**
		 * The main "filter id" used for {@link StatsTab.Filters}.
		 */
		public static final Identifier FID = fromNamespaceAndPath(MOD_ID, "entities_group_by");
		// --------------------------------------------------
		private final @NotNull Component name;
		private final @NotNull Function<Iterable<EntityStats>, LinkedHashMap<Component, Iterable<EntityStats>>> grouper;
		// ==================================================
		GroupBy(@NotNull Component name,
				@NotNull Function<Iterable<EntityStats>, LinkedHashMap<Component, Iterable<EntityStats>>> grouper) {
			this.name    = Objects.requireNonNull(name);
			this.grouper = grouper;
		}
		// ==================================================
		public final @Override @NotNull Component getDisplayName() { return this.name; }
		public final @NotNull Function<Iterable<EntityStats>, LinkedHashMap<Component, Iterable<EntityStats>>> getStatsGrouper() { return this.grouper; }
		// ==================================================
		/**
		 * Initializes GUI for the {@link StatsTabMobs.GroupBy} filter.
		 * @param context The {@link StatsTab.FiltersInitContext}.
		 * @throws NullPointerException If the argument is {@code null}.
		 */
		public static final void initFilter(@NotNull StatsTab.FiltersInitContext context) throws NullPointerException
		{
			//create and add the icon and widget
			final var panel = context.getPanel();
			final var nextY = StatsTabUtils.nextYBounds(panel, 20);

			final var icon = new TTextureElement(BSSSprites.gui_icon_filterGroup());
			icon.setBounds(nextY.x, nextY.y, 20, nextY.height);
			panel.add(icon);

			final var dropdown = new TDropdownWidget<StatsTabMobs.GroupBy>();
			dropdown.setBounds(nextY.x + 25, nextY.y, nextY.width - 25, nextY.height);
			Collections.addAll(dropdown.getEntries(), StatsTabMobs.GroupBy.values());
			dropdown.tooltipProperty().set(__ -> TTooltip.of(BSSLang.gui_statstab_filter_groupBy()), GroupBy.class);
			panel.add(dropdown);

			//set initial value and apply filters on value update
			dropdown.selectedEntryProperty().set(
					context.getFilters().getProperty(StatsTabMobs.GroupBy.class, FID, MOD),
					StatsTabMobs.GroupBy.class);
			dropdown.selectedEntryProperty().addChangeListener((p, o, n) -> {
				context.getFilters().setProperty(StatsTabMobs.GroupBy.class, FID, n);
				context.applyFilters();
			});
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
