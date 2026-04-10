package com.thecsdev.betterstats.api.mcbs.view.goal;

import com.thecsdev.betterstats.api.client.registry.BClientRegistries;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoal;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoalType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.thecsdev.betterstats.api.client.registry.BClientRegistries.GOAL_GUI;
import static java.util.Objects.requireNonNull;

/**
 * Client-sided factory for constructing GUIs for {@link McbsGoal}s.
 * @param <T> The {@link McbsGoal} type.
 */
@Environment(EnvType.CLIENT)
public abstract class McbsGoalGUI<T extends McbsGoal>
{
	// ==================================================
	private final @NotNull McbsGoalType<T> type;
	// ==================================================
	public McbsGoalGUI(@NotNull McbsGoalType<T> type) throws NullPointerException {
		this.type = requireNonNull(type);
	}
	// ==================================================
	/**
	 * Returns the {@link McbsGoalType} of the {@link McbsGoal} this
	 * {@link McbsGoalGUI} is for.
	 */
	public final @NotNull McbsGoalType<T> getType() { return this.type; }

	/**
	 * Returns the {@link Identifier} key for this {@link McbsGoalGUI} in the
	 * {@link BClientRegistries#GOAL_GUI} registry, or {@code null} if this
	 * {@link McbsGoalGUI} is not registered.
	 */
	public final @Nullable Identifier getKey() { return GOAL_GUI.getKey(this); }
	// ==================================================
	/**
	 * Finds and returns the {@link McbsGoalGUI} instance for a given {@link McbsGoal}.
	 * @param goal The {@link McbsGoal} to find the GUI for.
	 * @param <T> The {@link McbsGoal}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public static final @Nullable <T extends McbsGoal> McbsGoalGUI<T> findFor(@NotNull T goal) throws NullPointerException {
		//noinspection unchecked | the goal's constructor ensures the type is correct
		return findFor((McbsGoalType<T>) requireNonNull(goal).getType());
	}

	/**
	 * Finds and returns the {@link McbsGoalGUI} instance for a given {@link McbsGoalType}.
	 * @param type The {@link McbsGoalType} to find the GUI for.
	 * @param <T> The {@link McbsGoal} type.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public static final @Nullable <T extends McbsGoal> McbsGoalGUI<T> findFor(@NotNull McbsGoalType<T> type) throws NullPointerException
	{
		//argument must not be null
		requireNonNull(type);

		//obtain gui instance and ensure it matches
		final @Nullable var gui = GOAL_GUI.getValue(type.getKey());
		if(gui == null || gui.getType() != type) return null;

		//noinspection unchecked | already checked its class above
		return (McbsGoalGUI<T>) gui;
	}
	// ==================================================
}
