package com.thecsdev.betterstats.api.mcbs.model.goal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.thecsdev.betterstats.api.mcbs.model.McbsFile;
import com.thecsdev.common.util.annotations.Virtual;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.thecsdev.betterstats.api.registry.BRegistries.GOAL_TYPE;

/**
 * Represents a goal that a user seeks to accomplish for a given {@link McbsFile}.
 * <p>
 * Each goal has a progress value that ranges from 0 to 1, where 1 indicates that
 * the goal is completed.
 * <p>
 * The progress of a goal is determined by the implementation of the
 * {@link #getProgress(McbsFile)} method.
 */
public abstract class McbsGoal
{
	// ==================================================
	/**
	 * {@link Codec} instance for handling {@link McbsGoal} serialization.
	 */
	public static final Codec<? extends McbsGoal> CODEC = GOAL_TYPE.byNameCodec().partialDispatch(
			"type",
			goal -> DataResult.success(goal.getType()),
			type -> DataResult.success(type.getCodec()));
	// --------------------------------------------------
	private final @NotNull McbsGoalType<?> type;
	// ==================================================
	/**
	 * Creates an {@link McbsGoal} instance.
	 * @param type The corresponding {@link McbsGoalType} instance.
	 * @throws NullPointerException If the argument is {@code null}.
	 * @throws IllegalArgumentException If {@link McbsGoalType#getBaseClass()} does
	 *                                  not match {@code this.}{@link #getClass()}.
	 */
	public McbsGoal(@NotNull McbsGoalType<?> type) throws NullPointerException, IllegalArgumentException {
		Objects.requireNonNull(type);
		if(type.getBaseClass() != getClass())
			throw new IllegalArgumentException("Provided McbsGoalType doesn't represent this McbsGoal's Class");
		this.type = type;
	}
	// ==================================================
	/**
	 * Returns the {@link McbsGoalType} of this {@link McbsGoal}.
	 */
	public final @NotNull McbsGoalType<?> getType() { return this.type; }
	// --------------------------------------------------
	/**
	 * Returns a value ranging from 0 to 1, that indicates current progress towards
	 * completing the goal. A value of 1 indicates that the goal is completed.
	 * @param mcbsFile The {@link McbsFile} to check the progress for.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@Contract(pure = true)
	public abstract double getProgress(@NotNull McbsFile mcbsFile) throws NullPointerException;

	/**
	 * Returns visual user-friendly text that indicates current progress towards
	 * completing the goal. For example "15 / 25" or "1 / 3" and so on.
	 * @param mcbsFile The {@link McbsFile} to get the progress text for.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@Contract(pure = true)
	public abstract @NotNull Component getProgressText(@NotNull McbsFile mcbsFile)
			throws NullPointerException;

	/**
	 * Returns {@code true} only if {@link #getProgress(McbsFile)} is {@code >= 1.0}.
	 * @param mcbsFile The {@link McbsFile} to check the progress for.
	 */
	@Contract(pure = true)
	public final boolean isDone(@NotNull McbsFile mcbsFile) { return getProgress(mcbsFile) >= 1; }
	// --------------------------------------------------
	/**
	 * Returns the user-friendy display text for what the goal's "objective" is.<br>
	 * For example "Mine 100 Stone" or "Kill 30 Zombie" or "Travel 500 meters".
	 * <p>
	 * This differs from {@link McbsGoalType#getName()} in the fact that goal type name
	 * is a generic name that applies to all corresponding {@link McbsGoal} instances.
	 *
	 * @see McbsGoalType#getName()
	 */
	@Contract(pure = true)
	public @Virtual @NotNull Component getObjectiveText() { return getType().getName(); }
	// ==================================================
}
