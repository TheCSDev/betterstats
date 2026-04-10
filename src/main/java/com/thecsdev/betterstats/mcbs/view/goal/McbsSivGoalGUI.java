package com.thecsdev.betterstats.mcbs.view.goal;

import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoalType;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsSivGoal;
import com.thecsdev.betterstats.api.mcbs.view.goal.McbsGoalGUI;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.ApiStatus;

/**
 * {@link McbsGoalGUI} implementation for {@link McbsSivGoal}.
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class McbsSivGoalGUI extends McbsGoalGUI<McbsSivGoal>
{
	// ==================================================
	/**
	 * The main instance of this {@link Class}.
	 */
	public static final McbsSivGoalGUI INSTANCE = new McbsSivGoalGUI();
	// ==================================================
	private McbsSivGoalGUI() { super(McbsGoalType.STAT_INT_VALUE); }
	// ==================================================
}
