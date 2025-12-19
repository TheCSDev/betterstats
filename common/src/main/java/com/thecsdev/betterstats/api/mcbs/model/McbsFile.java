package com.thecsdev.betterstats.api.mcbs.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * This {@link Class} serves as the main MVC data structure for storing and managing all
 * statistics associated with a specific player.
 */
public final class McbsFile
{
	// ==================================================
	private final @NotNull McbsStats stats;
	// ==================================================
	public McbsFile() { this(new McbsStats()); }
	public McbsFile(@NotNull McbsStats stats) throws NullPointerException {
		this.stats = Objects.requireNonNull(stats);
	}
	// ==================================================
	/**
	 * Returns the {@link McbsStats} that holds statistics values.
	 */
	public final @NotNull McbsStats getStats() { return this.stats; }
	// ==================================================
}
