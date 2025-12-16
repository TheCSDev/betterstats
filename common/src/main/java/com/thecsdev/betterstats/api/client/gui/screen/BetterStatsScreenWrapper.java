package com.thecsdev.betterstats.api.client.gui.screen;

import com.thecsdev.commonmc.api.client.gui.screen.TScreen;
import com.thecsdev.commonmc.api.client.gui.screen.TScreenWrapper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * {@link BetterStatsScreen}'s {@link TScreenWrapper} implementation.
 */
@ApiStatus.Internal
public final class BetterStatsScreenWrapper extends TScreenWrapper<BetterStatsScreen>
{
	// ==================================================
	private final @NotNull BetterStatsScreen target;
	// ==================================================
	BetterStatsScreenWrapper(@NotNull BetterStatsScreen target) {
		super(target);
		this.target = target;
	}
	// ==================================================
	/**
	 * Returns the target {@link TScreen} as {@link BetterStatsScreen}.
	 * @see #getTargetTScreen()
	 */
	public final @NotNull BetterStatsScreen getTargetTScreenB() { return this.target; }
	// ==================================================
}
