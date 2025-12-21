package com.thecsdev.betterstats.client.gui.panel;

import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.widget.stats.TTextualStatWidget;
import com.thecsdev.commonmc.api.stats.util.EntityStats;
import com.thecsdev.commonmc.api.stats.util.ItemStats;
import com.thecsdev.commonmc.api.stats.util.SubjectStats;
import net.minecraft.stats.StatType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils.GAP;
import static com.thecsdev.commonmc.api.stats.IStatsProvider.getStatTypeName;
import static net.minecraft.network.chat.Component.literal;

/**
 * {@link TPanelElement} that displays {@link TTextualStatWidget} elements
 * that aim to summarize the statistics on a given {@link StatsView}.
 */
@ApiStatus.Internal
public final class StatsSummaryPanel extends TPanelElement.Paintable
{
	// ==================================================
	private static final int ENTRY_HEIGHT = 16;
	// --------------------------------------------------
	private final Collection<TTextualStatWidget> entries = new ArrayList<>();
	// ==================================================
	public StatsSummaryPanel() {
		scrollPaddingProperty().set(5, StatsSummaryPanel.class);
		outlineColorProperty().set(0xFF000000, StatsSummaryPanel.class);
	}

	public StatsSummaryPanel(@NotNull Collection<? extends SubjectStats<?>> stats)
	{
		//initialize 'this'
		this();

		//summarize item stats and iterate all entries
		for(final var entry : summarizeStats(stats).entrySet())
		{
			//create and set up a custom stat widget for each entry
			final var widget = new TTextualStatWidget();
			widget.focusableProperty().set(false, StatsSummaryPanel.class);
			widget.hoverableProperty().set(false, StatsSummaryPanel.class);
			widget.backgroundColorProperty().set(0, StatsSummaryPanel.class);
			widget.outlineColorProperty().set(0, StatsSummaryPanel.class);
			//initialize label texts
			widget.getKeyLabel().textProperty().set(
					getStatTypeName(entry.getKey()), StatsSummaryPanel.class);
			widget.getValueLabel().textProperty().set(
					literal(Integer.toString(entry.getValue())), StatsSummaryPanel.class);
			//TODO - ^ IMPLEMENT STATS FORMATTER!
			//add the entry widget to the collection of entries
			this.entries.add(widget);
		}
	}
	// ==================================================
	/**
	 * Returns the entries that are to be included by this {@link StatsSummaryPanel}.
	 * The entries are {@link TTextualStatWidget} elements that have custom
	 * name and value labels, that are then automatically placed into this
	 * {@link StatsSummaryPanel} during {@link #initCallback()}.
	 * @see TTextualStatWidget#getKeyLabel()
	 * @see TTextualStatWidget#getValueLabel()
	 */
	public final Collection<TTextualStatWidget> getEntries() { return entries; }
	// ==================================================
	protected final @Override void initCallback()
	{
		//prepare by obtaining the bounding box
		final var bb = getBounds();
		//if this panel is too small, put all entries in one column
		if(bb.width < 400) {
			for(final var entry : getEntries()) {
				entry.setBounds(computeNextYBounds(ENTRY_HEIGHT, GAP));
				add(entry);
			}
		//else put entries in two columns
		} else {
			final int padding   = scrollPaddingProperty().getI();
			final int halfWidth = (bb.width - GAP - padding * 2) / 2;
			int       leftY     = bb.y + padding;
			int       rightY    = bb.y + padding;
			boolean   leftSide  = true;
			for(final var entry : getEntries()) {
				if(leftSide) {
					entry.setBounds(bb.x + padding, leftY, halfWidth, ENTRY_HEIGHT);
					leftY += ENTRY_HEIGHT + GAP;
				} else {
					entry.setBounds(bb.x + padding + halfWidth + GAP, rightY, halfWidth, ENTRY_HEIGHT);
					rightY += ENTRY_HEIGHT + GAP;
				}
				leftSide = !leftSide;
				add(entry);
			}
		}
	}
	// ==================================================
	/**
	 * Summarizes a {@link Collection} of {@link ItemStats} into a
	 * {@link LinkedHashMap} that maps each {@link StatType<Item>} to
	 * the total integer value of that statistic across all provided
	 * {@link ItemStats}.
	 * @param stats The collection of {@link ItemStats} to summarize.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@SuppressWarnings("unchecked")
	public static final @NotNull LinkedHashMap<StatType<Item>, Integer> summarizeItemStats(
			@NotNull Collection<ItemStats> stats) throws NullPointerException {
		return (LinkedHashMap<StatType<Item>, Integer>)
				(LinkedHashMap<?, ?>) summarizeStats(Objects.requireNonNull(stats));
	}

	/**
	 * Summarizes a {@link Collection} of {@link EntityStats} into a
	 * {@link LinkedHashMap} that maps each {@link StatType} to
	 * the total integer value of that statistic across all provided
	 * {@link EntityStats}.
	 * @param stats The collection of {@link EntityStats} to summarize.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@SuppressWarnings("unchecked")
	public static final @NotNull LinkedHashMap<StatType<EntityType<?>>, Integer> summarizeEntityStats(
			@NotNull Collection<EntityStats> stats) throws NullPointerException {
		return (LinkedHashMap<StatType<EntityType<?>>, Integer>)
				(LinkedHashMap<?, ?>) summarizeStats(Objects.requireNonNull(stats));
	}

	/**
	 * Summarizes a {@link Collection} of {@link SubjectStats} into a
	 * {@link LinkedHashMap} that maps each {@link StatType} to
	 * the total integer value of that statistic across all provided
	 * {@link SubjectStats}.
	 * @param stats The collection of {@link SubjectStats} to summarize.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public static final @NotNull LinkedHashMap<StatType<?>, Integer> summarizeStats(
			@NotNull Collection<? extends SubjectStats<?>> stats) throws NullPointerException
	{
		return Objects.requireNonNull(stats).stream()
				.flatMap(stat -> stat.getValues().entrySet().stream())
				.collect(Collectors.groupingBy(
						entry -> entry.getKey().getType(),
						LinkedHashMap::new,
						Collectors.summingInt(Map.Entry::getValue)
				));
	}
	// --------------------------------------------------
	/**
	 * Initializes a {@link StatsSummaryPanel} on the given target
	 * {@link TPanelElement}, using the provided {@link SubjectStats}
	 * collection as the source of data to summarize.
	 * @param target The target {@link TPanelElement} to initialize the {@link StatsSummaryPanel} on.
	 * @param stats The collection of {@link SubjectStats} to summarize.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public static final void initPanel(
			@NotNull TPanelElement target,
			@NotNull Collection<? extends SubjectStats<?>> stats)
	{
		//not null requirements, and stats size requirement
		Objects.requireNonNull(target);
		Objects.requireNonNull(stats);
		if(stats.isEmpty()) return;

		//create the summary panel
		final var panel = new StatsSummaryPanel(stats);

		//"mock"-initialize the panel to calculate its needed space
		panel.setBounds(target.computeNextYBounds(20, GAP));
		panel.clearAndInit();
		final var pbb = panel.getBounds(); final var cbb = panel.getContentBounds();
		panel.setBounds(pbb.x, pbb.y, pbb.width, cbb.height + panel.scrollPaddingProperty().getI() * 2);
		panel.clear(); //clear the initialization once done. it'll be reinitialized later

		//add the summary panel to the target
		target.add(panel);
	}
	// ==================================================
}