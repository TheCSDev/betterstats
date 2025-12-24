package com.thecsdev.betterstats.api.mcbs.model;

import org.jetbrains.annotations.NotNull;

/**
 * This {@link Class} serves as the main MVC data structure for storing and managing all
 * statistics associated with a specific player.
 */
public final class McbsFile
{
	// ==================================================
	private final @NotNull McbsStats stats = new McbsStats();
	// ==================================================
	/**
	 * Returns the {@link McbsStats} that holds statistics values.
	 */
	public final @NotNull McbsStats getStats() { return this.stats; }
	// ==================================================
}
