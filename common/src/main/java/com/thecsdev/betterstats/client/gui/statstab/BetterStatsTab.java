package com.thecsdev.betterstats.client.gui.statstab;

import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.api.client.gui.statstab.StatsTab;
import com.thecsdev.common.util.annotations.Virtual;
import com.thecsdev.commonmc.api.stats.util.SubjectStats;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.thecsdev.betterstats.api.client.gui.statstab.StatsTabUtils.FID_EMPTYSTATS;
import static com.thecsdev.betterstats.api.client.gui.statstab.StatsTabUtils.FID_SEARCH;

/**
 * Base {@link Class} for {@link BetterStats}'s {@link StatsTab} implementations.
 */
@ApiStatus.Internal
abstract class BetterStatsTab<SS extends SubjectStats<?>> extends StatsTab
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
	 * this {@link BetterStatsTab}'s "Statistics" panel.
	 * @param filters The {@link StatsTab.Filters}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	protected @Virtual @NotNull Predicate<SS> getStatsPredicate(@NotNull StatsTab.Filters filters) throws NullPointerException {
		//get filter values
		final var f_query = filters.getProperty(String.class, FID_SEARCH, "");
		final var f_empty = filters.getProperty(Boolean.class, FID_EMPTYSTATS, false);
		//construct and return predicate
		return stat -> stat.isSearchMatch(f_query) && (f_empty || !stat.isEmpty());
	}

	/**
	 * The {@link Comparator} used for sorting {@link SubjectStats} shown on
	 * this {@link BetterStatsTab}'s "Statistics" panel.
	 * @param filters The {@link StatsTab.Filters}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	protected @Virtual @NotNull Comparator<SS> getStatsSorter(
			@NotNull StatsTab.Filters filters) throws NullPointerException {
		return Comparator.comparing(stat -> stat.getSubjectDisplayName().getString());
	}

	/**
	 * The {@link Function} that takes a collection of {@link SubjectStats} and
	 * groups them into a {@link Map} based on the {@link StatsTab.Filters}.
	 * @param filters The {@link StatsTab.Filters}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	protected @Virtual @NotNull Function<Iterable<SS>, LinkedHashMap<Component, Iterable<SS>>> getStatsGrouper(
			@NotNull StatsTab.Filters filters) {
		return in -> {
			final var map = new LinkedHashMap<Component, Iterable<SS>>();
			map.put(Component.literal("*"), Objects.requireNonNull(in));
			return map;
		};
	}
	// ==================================================
}
