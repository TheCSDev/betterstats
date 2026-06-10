package com.thecsdev.betterstats.api.mcbs.model.goal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thecsdev.betterstats.api.mcbs.model.McbsFile;
import com.thecsdev.betterstats.api.mcbs.model.McbsStats;
import com.thecsdev.betterstats.resource.BLanguage;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

import static com.thecsdev.betterstats.BetterStats.MOD_ID;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;
import static net.minecraft.resources.Identifier.withDefaultNamespace;

/**
 * {@link McbsGoal} implementation whose goal is to achieve a specific
 * stat integer value.
 */
@SuppressWarnings("NotNullFieldNotInitialized")
public final class McbsSivGoal extends McbsGoal
{
	// ==================================================
	/**
	 * {@link MapCodec} implementation for serializing {@link McbsSivGoal} instances.
	 */
	public static final MapCodec<McbsSivGoal> CODEC = RecordCodecBuilder.mapCodec(instance ->
		instance.group(
				Identifier.CODEC.fieldOf("stat_type").forGetter(McbsSivGoal::getStatType),
				Identifier.CODEC.fieldOf("stat_subject").forGetter(McbsSivGoal::getStatSubject),
				Codec.INT.lenientOptionalFieldOf("from_value", 0).forGetter(McbsSivGoal::getFromValue),
				Codec.INT.fieldOf("target_value").forGetter(McbsSivGoal::getTargetValue)
		).apply(instance, McbsSivGoal::new)
	);

	/**
	 * {@link #getStatType()} value that is used for {@link McbsSivGoal} instances
	 * constructed using the default {@link #McbsSivGoal()} constructor.
	 */
	public static final Identifier STID_EDITTHISGOAL = fromNamespaceAndPath(MOD_ID, "edit_this_goal");
	// ==================================================
	private @NotNull Identifier statType;     //ex. "Times used"
	private @NotNull Identifier statSubject;  //ex. "Apple"
	private          int        fromValue;    //ex. "from 50"
	private          int        targetValue;  //ex. "to 125" - from 50 to 125 becomes "Use 75 Apple"
	// ==================================================
	public McbsSivGoal() { this(STID_EDITTHISGOAL, withDefaultNamespace("air"), 0, 1); }

	public McbsSivGoal(
			@NotNull Identifier statType, @NotNull Identifier statSubject,
			int targetValue) throws NullPointerException {
		this(statType, statSubject, 0, targetValue);
	}

	public McbsSivGoal(
			@NotNull Identifier statType, @NotNull Identifier statSubject,
			int fromValue, int targetValue) throws NullPointerException
	{
		super(McbsGoalType.STAT_INT_VALUE);
		this.statType    = Objects.requireNonNull(statType);
		this.statSubject = Objects.requireNonNull(statSubject);
		this.fromValue   = Math.abs(fromValue);
		this.targetValue = Math.abs(targetValue);
	}

	public McbsSivGoal(@NotNull McbsSivGoal copyFrom) {
		super(McbsGoalType.STAT_INT_VALUE);
		copyFrom(copyFrom);
	}
	// ==================================================
	@SuppressWarnings("MethodDoesntCallSuperMethod")
	public final @Override @NonNull McbsSivGoal clone() {
		final var copy = new McbsSivGoal();
		copy.copyFrom(this);
		return copy;
	}
	// ==================================================
	/**
	 * Returns the {@link StatType}'s unique {@link Identifier}.
	 * @see McbsStats#getIntValue(Identifier, Identifier)
	 */
	public final @NotNull Identifier getStatType() { return this.statType; }

	/**
	 * The statistic subject's unique identifier.
	 * @see McbsStats#getIntValue(Identifier, Identifier)
	 */
	public final @NotNull Identifier getStatSubject() { return this.statSubject; }

	/**
	 * The "starting point" value for this goal. This is the value that the
	 * progress calculation will consider as "0% progress".
	 */
	public final int getFromValue() { return this.fromValue; }

	/**
	 * The "target" value for this goal. This is the value that the progress
	 * calculation will consider as "100% progress".
	 */
	public final int getTargetValue() { return this.targetValue; }
	// --------------------------------------------------
	/**
	 * Sets the value of {@link #getStatType()}.
	 * @param statType The {@link StatType}'s unique identifier.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public final void setStatType(@NotNull Identifier statType) throws NullPointerException {
		this.statType = Objects.requireNonNull(statType);
	}

	/**
	 * Sets the value of {@link #getStatSubject()}
	 * @param statSubject The statistic subject.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public final void setStatSubject(@NotNull Identifier statSubject) throws NullPointerException {
		this.statSubject = Objects.requireNonNull(statSubject);
	}

	/**
	 * Sets the value of {@link #getFromValue()}.
	 * @param fromValue The "starting point" value for this goal.
	 */
	public final void setFromValue(int fromValue) { this.fromValue = fromValue; }

	/**
	 * Sets the value of {@link #getTargetValue()}.
	 * @param targetValue The "target" value for this goal.
	 */
	public final void setTargetValue(int targetValue) { this.targetValue = targetValue; }
	// ==================================================
	public final @Override double getProgress(@NotNull McbsFile mcbsFile) throws NullPointerException
	{
		//explicitly cast values to 'double'
		final double v = mcbsFile.getStats().getIntValue(this.statType, this.statSubject);
		final double f = this.fromValue;
		final double t = this.targetValue;
		//edge-case accounting for division by 0
		if(Math.abs(t - f) < 1e-9) return 1d;
		//calculate via 'linear interpolation' and return
		return Math.clamp((v - f) / (t - f), 0d, 1d);
	}

	public final @Override @NotNull Component getProgressText(
			@NotNull McbsFile mcbsFile) throws NullPointerException
	{
		final int statVal = mcbsFile.getStats().getIntValue(this.statType, this.statSubject);
		final int outOf   = Math.max(this.targetValue - this.fromValue, 0);
		final int got     = Math.clamp(statVal - this.fromValue, Integer.MIN_VALUE, outOf);
		return Component.literal(got + " / " + outOf);
	}
	// --------------------------------------------------
	public final @Override @NotNull Component getObjectiveText() {
		return BLanguage.mcbsgoal_sivObjectiveText(this);
	}
	// ==================================================
	/**
	 * Copies values of all fields from the provided {@link McbsSivGoal}
	 * into this {@link McbsSivGoal}.
	 * @param other The {@link McbsSivGoal} to copy values from.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public final void copyFrom(@NotNull McbsSivGoal other) throws NullPointerException {
		this.statType    = other.statType;
		this.statSubject = other.statSubject;
		this.fromValue   = other.fromValue;
		this.targetValue = other.targetValue;
	}
	// ==================================================
}
