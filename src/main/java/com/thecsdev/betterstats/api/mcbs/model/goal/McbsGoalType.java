package com.thecsdev.betterstats.api.mcbs.model.goal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a type of {@link McbsGoal}. Holds information about a given type, and a
 * {@link Codec} for serializing and deserializing instances of that {@link Class}.
 * @param <T> The {@link McbsGoal} type.
 */
public abstract class McbsGoalType<T extends McbsGoal>
{
	// ==================================================
	/**
	 * {@link McbsGoalType} implementation for {@link McbsSivGoal}.
	 */
	public static final McbsGoalType<McbsSivGoal> STAT_INT_VALUE = new McbsGoalType<>(McbsSivGoal.class) {
		public final @Override @NotNull MapCodec<McbsSivGoal> getCodec() { return McbsSivGoal.CODEC; }
	};
	// ==================================================
	private final @NotNull Class<T> baseClass;
	// ==================================================
	public McbsGoalType(@NotNull Class<T> baseClass) throws NullPointerException {
		this.baseClass = Objects.requireNonNull(baseClass);
	}
	// ==================================================
	/**
	 * Returns the {@link Class} of the {@link McbsGoal} this {@link McbsGoalType}
	 * represents.
	 */
	public final @NotNull Class<T> getBaseClass() { return this.baseClass; }
	// --------------------------------------------------
	/**
	 * Returns the {@link Codec} used for serializing and deserializing {@link McbsGoal}s
	 * of this {@link McbsGoalType}.
	 */
	public abstract @NotNull MapCodec<T> getCodec();
	// ==================================================
}
