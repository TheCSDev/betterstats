package com.thecsdev.betterstats.api.mcbs.model;

import com.thecsdev.commonmc.api.stats.IStatsProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Container for holding statistics values, used by {@link McbsFile}.
 */
public final class McbsStats implements IStatsProvider
{
	// ================================================== ==================================================
	//                                          McbsStats IMPLEMENTATION
	// ================================================== ==================================================
	private final ConcurrentHashMap<Identifier, ConcurrentHashMap<Identifier, Integer>> intStats = new ConcurrentHashMap<>();
	// ==================================================
	public McbsStats() {}
	public McbsStats(@NotNull IStatsProvider copyFrom) { clearAndAddAll(Objects.requireNonNull(copyFrom)); }
	// ==================================================
	/**
	 * Clears redundant statistic data like zero-value entries and empty maps.
	 */
	private final @ApiStatus.Internal void cleanUp()
	{
		//remove zero-value statistic entries
		for(final var statTypeEntry : this.intStats.entrySet())
			statTypeEntry.getValue().entrySet().removeIf(e -> e.getValue() == 0);
		//remove empty stat-type maps
		this.intStats.entrySet().removeIf(e -> e.getValue().isEmpty());
	}
	// ==================================================
	/**
	 * Returns raw direct access to the main {@link Map} that holds all
	 * integer-based statistics values.
	 */
	public final @NotNull ConcurrentHashMap<Identifier, ConcurrentHashMap<Identifier, Integer>> getIntValues() {
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
			@NotNull Identifier type) throws NullPointerException
	{
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
	public final @Override <T> int getIntValue(@NotNull Stat<T> stat) throws NullPointerException {
		return getIntValue(stat.getType(), stat.getValue());
	}

	/**
	 * Returns the value of a given integer-based statistic.
	 * @param type The {@link StatType}.
	 * @param subject The statistic subject.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @throws IllegalStateException If a corresponding feature is not registered.
	 */
	public final @Override <T> int getIntValue(@NotNull StatType<T> type, @NotNull T subject)
			throws NullPointerException, IllegalStateException
	{
		//obtain id-s from registries
		final @Nullable var statTypeId    = BuiltInRegistries.STAT_TYPE.getKey(Objects.requireNonNull(type));
		final @Nullable var statSubjectId = type.getRegistry().getKey(Objects.requireNonNull(subject));
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
		return getIntValues(Objects.requireNonNull(type)).getOrDefault(Objects.requireNonNull(subject), 0);
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
	public final <T> void setIntValue(
			@NotNull StatType<T> type, @NotNull T subject, int value)
			throws NullPointerException, IllegalStateException
	{
		//obtain id-s from registries
		final @Nullable var statTypeId    = BuiltInRegistries.STAT_TYPE.getKey(Objects.requireNonNull(type));
		final @Nullable var statSubjectId = type.getRegistry().getKey(Objects.requireNonNull(subject));
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
	public final void setIntValue(
			@NotNull Identifier type, @NotNull Identifier subject, int value)
			throws NullPointerException
	{
		if(value == 0) {
			final var map = getIntValues(Objects.requireNonNull(type));
			map.remove(Objects.requireNonNull(subject));
			if(map.isEmpty()) this.intStats.remove(type);
		}
		else getIntValues(Objects.requireNonNull(type)).put(Objects.requireNonNull(subject), value);
	}
	// ==================================================
	/**
	 * Adds {@link Stat} entries from another {@link IStatsProvider} into this one,
	 * summing up values where applicable. Supports {@link McbsStats} instances as
	 * argument.
	 * @param statsProvider The other {@link IStatsProvider}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@SuppressWarnings("unchecked")
	public final void addAll(@NotNull IStatsProvider statsProvider)
	{
		//argument not null check
		Objects.requireNonNull(statsProvider);
		//handle mcbs stats providers by adding all their values here
		if(statsProvider instanceof McbsStats other) {
			other.forEach((statTypeId, statSubjectId, otherValue) -> {
				final int thisValue = getIntValue(statTypeId, statSubjectId);
				setIntValue(statTypeId, statSubjectId, thisValue + otherValue);
			});
			return;
		}
		//handle all other stat provider types by iterating game's registries
		//and querying values manually
		for(final var statType : (Registry<StatType<Object>>)(Object) BuiltInRegistries.STAT_TYPE) {
			for(final var statSubject : statType.getRegistry()) {
				//get value and add it to this mcbs stats provider
				final int otherValue = statsProvider.getIntValue(statType, statSubject);
				if(otherValue == 0) continue;
				final int thisValue = getIntValue(statType, statSubject);
				setIntValue(statType, statSubject, thisValue + otherValue);
			}
		}
	}

	/**
	 * Clears all existing {@link Stat} entries and adds all entries from
	 * another {@link IStatsProvider}.
	 * @param statsProvider The other {@link IStatsProvider}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public final void clearAndAddAll(@NotNull IStatsProvider statsProvider) throws NullPointerException {
		Objects.requireNonNull(statsProvider);
		clear();
		addAll(statsProvider);
	}
	// ==================================================
	/**
	 * Clears all statistics data.
	 */
	public final void clear() { this.intStats.clear(); }
	// --------------------------------------------------
	/**
	 * Iterates all integer values stored in this {@link McbsStats} instance.
	 * Note that this does not offer value manipulation capabilities.
	 * @param consumer The consumer that will consume each value.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public final void forEach(@NotNull McbsStats.IntValueConsumer consumer) throws NullPointerException
	{
		Objects.requireNonNull(consumer);
		cleanUp();
		for(final var statTypeEntry : this.intStats.entrySet()) {
			final var statTypeId = statTypeEntry.getKey();
			for(final var statSubjectEntry : statTypeEntry.getValue().entrySet()) {
				final var statSubjectId = statSubjectEntry.getKey();
				final int value = statSubjectEntry.getValue();
				consumer.accept(statTypeId, statSubjectId, value);
			}
		}
	}
	// ================================================== ==================================================
	//                                   IntEntryConsumer IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link FunctionalInterface} for consuming integer values stored in an
	 * {@link McbsStats} instance.
	 * @see #forEach(IntValueConsumer)
	 */
	public static @FunctionalInterface interface IntValueConsumer
	{
		/**
		 * Consumes a single {@link Stat} entry.
		 * @param statType The {@link StatType}'s unique identifier.
		 * @param statSubject The {@link Stat} subject's unique identifier.
		 * @param value The {@link Stat}'s value.
		 * @throws NullPointerException If an argument is {@code null}.
		 */
		void accept(@NotNull Identifier statType, @NotNull Identifier statSubject, int value);
	}
	// ================================================== ==================================================
}
