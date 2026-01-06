package com.thecsdev.betterstats.mcbs.view.statsview;

import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.common.util.annotations.Virtual;
import com.thecsdev.commonmc.api.stats.util.SubjectStats;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils.FID_EMPTYSTATS;
import static com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils.FID_SEARCH;

/*
FIXME - Question is, do I use 'TCDCommons API' SubjectStats or do I deprecate and delete
        it in favor of ability to show all McbsStats regardless of whether their subjects
        exist in-game's registries?
*/

/**
 * Base {@link Class} for {@link BetterStats}'s {@link StatsView} implementations.
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public abstract sealed class SubjectStatsView<SS extends SubjectStats<?>> extends StatsView
		permits StatsViewGeneral, StatsViewItems, StatsViewMobs
{
	// ==================================================
	/**
	 * An {@link Integer} corresponding to the outline color that is to be
	 * used for statistics widgets that aren't empty but aren't {@link #OC_DONE}
	 * either.
	 * @see SubjectStats#isEmpty()
	 */
	public static final @ApiStatus.Internal int OC_INPROGRESS = 0x35FFFFFF;

	/**
	 * An {@link Integer} corresponding to the outline color that is to be
	 * used for statistics widgets that are considered "complete", usually for
	 * advancement progress tracking (ex. diet and monster hunting).
	 */
	public static final @ApiStatus.Internal int OC_DONE = 0xFFFFFF00;
	// ==================================================
	/**
	 * The {@link Predicate} used for filtering {@link SubjectStats} shown on
	 * this {@link SubjectStatsView}'s "Statistics" panel.
	 * @param filters The {@link StatsView.Filters}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	protected @Virtual @NotNull Predicate<SS> getStatsPredicate(
			@NotNull StatsView.Filters filters) throws NullPointerException
	{
		Objects.requireNonNull(filters);
		//get filter values
		final var f_query = filters.getProperty(String.class, FID_SEARCH, "");
		final var f_empty = filters.getProperty(Boolean.class, FID_EMPTYSTATS, false);
		//construct and return predicate
		return stat -> stat.isSearchMatch(f_query) && (f_empty || !stat.isEmpty());
	}

	/**
	 * The {@link Comparator} used for sorting {@link SubjectStats} shown on
	 * this {@link SubjectStatsView}'s "Statistics" panel.
	 * @param filters The {@link StatsView.Filters}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	protected @Virtual @NotNull Comparator<SS> getStatsSorter(
			@NotNull StatsView.Filters filters) throws NullPointerException
	{
		Objects.requireNonNull(filters);
		return Comparator.comparing(stat -> stat.getSubjectDisplayName().getString());
	}

	/**
	 * The {@link Function} that takes a collection of {@link SubjectStats} and
	 * groups them into a {@link Map} based on the {@link StatsView.Filters}.
	 * @param filters The {@link StatsView.Filters}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	protected @Virtual @NotNull Function<Iterable<SS>, LinkedHashMap<Component, Iterable<SS>>> getStatsGrouper(
			@NotNull StatsView.Filters filters) throws NullPointerException
	{
		Objects.requireNonNull(filters);
		return in -> {
			final var map = new LinkedHashMap<Component, Iterable<SS>>();
			map.put(Component.literal("*"), Objects.requireNonNull(in));
			return map;
		};
	}
	// ==================================================
}
