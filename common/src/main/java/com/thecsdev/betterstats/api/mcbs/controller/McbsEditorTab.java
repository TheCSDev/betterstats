package com.thecsdev.betterstats.api.mcbs.controller;

import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.betterstats.api.mcbs.model.McbsFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * This {@link Class} serves as the controller component in the MVC architecture,
 * managing interactions and operations related to a specific {@link McbsFile}
 * instance.
 */
public final class McbsEditorTab
{
	// ==================================================
	private final @NotNull  McbsFile          mcbsFile;
	// --------------------------------------------------
	private final @Nullable StatsView         currentView = null;
	private final @NotNull  StatsView.Filters filters     = new StatsView.Filters();
	// ==================================================
	public McbsEditorTab(@NotNull McbsFile mcbsFile) throws NullPointerException {
		this.mcbsFile = Objects.requireNonNull(mcbsFile);
	}
	// ==================================================
	/**
	 * Returns the {@link StatsView} instance that is currently selected for
	 * this {@link McbsEditorTab}.
	 */
	public final @Nullable StatsView getCurrentView() { return this.currentView; }

	/**
	 * Returns the {@link StatsView.Filters} instance that is used for
	 * this {@link McbsEditorTab}.
	 */
	public final @NotNull StatsView.Filters getFilters() { return this.filters; }
	// ==================================================
}
