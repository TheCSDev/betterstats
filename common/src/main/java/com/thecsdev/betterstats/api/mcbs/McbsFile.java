package com.thecsdev.betterstats.api.mcbs;

import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.commonmc.api.stats.StatsProvider;
import io.netty.util.internal.UnstableApi;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * {@link BetterStats}'s file format for storing player statistics data.
 */
@UnstableApi
@ApiStatus.Experimental
public final class McbsFile
{
	// ================================================== ==================================================
	//                                           McbsFile IMPLEMENTATION
	// ================================================== ==================================================
	private @NotNull McbsStats stats;
	// ==================================================
	public McbsFile() { this(null); }
	public McbsFile(@Nullable StatsProvider statsProvider) { this.stats = new McbsStats(statsProvider); }
	// ==================================================
	/**
	 * Returns the {@link McbsStats} instance holding all statistics data.
	 */
	public final @NotNull McbsStats getStats() { return this.stats; }

	/**
	 * Sets the {@link McbsStats} instance holding all statistics data.
	 * @param stats The new {@link McbsStats} instance.
	 * @throws NullPointerException If the argument {@code null}.
	 */
	public final void setStats(@NotNull McbsStats stats) throws NullPointerException {
		this.stats = Objects.requireNonNull(stats);
	}
	// ================================================== ==================================================
}
