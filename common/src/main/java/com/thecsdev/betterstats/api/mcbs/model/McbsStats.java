package com.thecsdev.betterstats.api.mcbs.model;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

/**
 * Container for holding statistics values, used by {@link McbsFile}.
 */
public final class McbsStats
{
	// ==================================================
	private final ConcurrentHashMap<Identifier, ConcurrentHashMap<Identifier, Integer>> intStats = new ConcurrentHashMap<>();
	// ==================================================
	/**
	 * Returns raw direct access to the main {@link Map} that holds all
	 * integer-based statistics values.
	 */
	public final @NotNull ConcurrentHashMap<Identifier, ConcurrentHashMap<Identifier, Integer>> getIntStats() {
		cleanUp();
		return this.intStats;
	}

	/**
	 * Returns the {@link Map} that contains integer-based statistics values for
	 * a given {@link StatType}.
	 * @param type The {@link StatType}'s unique identifier.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public final @NotNull ConcurrentHashMap<Identifier, Integer> getIntValues(
			@NotNull Identifier type) throws NullPointerException {
		Objects.requireNonNull(type);
		cleanUp();
		return this.intStats.computeIfAbsent(type, __ -> new ConcurrentHashMap<>());
	}
	// ==================================================
	/**
	 * Returns the value of a given integer-based statistic.
	 * @param stat The {@link Stat} whose value is to be returned.
	 * @throws NullPointerException If the argument is {@code null}.
	 * @throws IllegalStateException If a corresponding feature is not registered.
	 */
	public final <T> int getIntValue(@NotNull Stat<T> stat) throws NullPointerException {
		return getIntValue(stat.getType(), stat.getValue());
	}

	/**
	 * Returns the value of a given integer-based statistic.
	 * @param type The {@link StatType}.
	 * @param subject The statistic subject.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @throws IllegalStateException If a corresponding feature is not registered.
	 */
	public final <T> int getIntValue(@NotNull StatType<T> type, @NotNull T subject)
			throws NullPointerException, IllegalStateException
	{
		//obtain id-s from registries
		final @Nullable var statTypeId    = BuiltInRegistries.STAT_TYPE.getKey(requireNonNull(type));
		final @Nullable var statSubjectId = type.getRegistry().getKey(requireNonNull(subject));
		//ensure the id-s are not null
		if(statTypeId == null)
			throw new IllegalStateException("StatType not registered: " + type);
		else if(statSubjectId == null)
			throw new IllegalStateException("Feature not registered: " + subject);
		//get and return the stat value
		return getIntValue(statTypeId, statSubjectId);
	}

	/**
	 * Returns the value of a given integer-based statistic.
	 * @param type The {@link StatType}'s unique identifier.
	 * @param subject The statistic subject's unique identifier.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public final int getIntValue(@NotNull Identifier type, @NotNull Identifier subject)
			throws NullPointerException {
		return getIntValues(requireNonNull(type)).getOrDefault(requireNonNull(subject), 0);
	}
	// ==================================================
	/**
	 * Sets the value of a given integer-based statistic.
	 * @param stat The {@link Stat} whose value is to be set.
	 * @param value The value to set.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public final <T> void setIntValue(@NotNull Stat<T> stat, int value) throws NullPointerException {
		setIntValue(stat.getType(), stat.getValue(), value);
	}

	/**
	 * Sets the value of a given integer-based statistic.
	 * @param type The {@link StatType}.
	 * @param subject The statistic subject.
	 * @param value The value to set.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @throws IllegalStateException If a corresponding feature is not registered.
	 */
	public final <T> void setIntValue(@NotNull StatType<T> type, @NotNull T subject, int value)
			throws NullPointerException, IllegalStateException
	{
		//obtain id-s from registries
		final @Nullable var statTypeId    = BuiltInRegistries.STAT_TYPE.getKey(requireNonNull(type));
		final @Nullable var statSubjectId = type.getRegistry().getKey(requireNonNull(subject));
		//ensure the id-s are not null
		if(statTypeId == null)
			throw new IllegalStateException("StatType not registered: " + type);
		else if(statSubjectId == null)
			throw new IllegalStateException("Feature not registered: " + subject);
		//set the stat value
		setIntValue(statTypeId, statSubjectId, value);
	}

	/**
	 * Sets the value of a given integer-based statistic.
	 * @param type The {@link StatType}'s unique identifier.
	 * @param subject The statistic subject's unique identifier.
	 * @param value The value to set.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public final void setIntValue(@NotNull Identifier type, @NotNull Identifier subject, int value)
			throws NullPointerException {
		if(value == 0) {
			final var map = getIntValues(requireNonNull(type));
			map.remove(requireNonNull(subject));
			if(map.isEmpty()) this.intStats.remove(type);
		}
		else getIntValues(requireNonNull(type)).put(requireNonNull(subject), value);
	}
	// ==================================================
	/**
	 * Cleans up the statistics model by removing all entries with a value of zero.
	 */
	public final void cleanUp()
	{
		//remove zero-value statistic entries
		for(final var statTypeEntry : this.intStats.entrySet())
			statTypeEntry.getValue().entrySet().removeIf(e -> e.getValue() == 0);
		//remove empty stat-type maps
		this.intStats.entrySet().removeIf(e -> e.getValue().isEmpty());
	}
	// ==================================================
}
