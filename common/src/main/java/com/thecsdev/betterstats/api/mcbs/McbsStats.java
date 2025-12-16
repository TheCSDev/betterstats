package com.thecsdev.betterstats.api.mcbs;

import com.thecsdev.commonmc.api.client.stats.LocalPlayerStatsProvider;
import com.thecsdev.commonmc.api.stats.StatsProvider;
import io.netty.util.internal.UnstableApi;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Storage implementation that stores {@link Stat} values for a given {@link McbsFile}
 * using {@link Map}s and {@link Object2IntMap}s.
 */
@UnstableApi
@ApiStatus.Experimental
public final class McbsStats extends StatsProvider
{
	// ================================================== ==================================================
	//                                          McbsStats IMPLEMENTATION
	// ================================================== ==================================================
	// NOTE TO DEV.: All operations done on this map shall use synchronized blocks.
	private final HashMap<Identifier, Object2IntOpenHashMap<Identifier>> stats = new HashMap<>();
	// --------------------------------------------------
	private final transient boolean isOfLocalPlayer;
	// ==================================================
	public McbsStats() { this(null); }
	public McbsStats(@Nullable StatsProvider statsProvider) {
		this.isOfLocalPlayer = (statsProvider instanceof LocalPlayerStatsProvider);
		if(statsProvider != null) setAll(statsProvider);
	}
	// ==================================================
	/**
	 * Returns {@code true} if this {@link McbsStats} instance was constructed
	 * using a {@link LocalPlayerStatsProvider}.
	 */
	public final boolean isOfLocalPlayer() { return this.isOfLocalPlayer; }

	/**
	 * Returns the {@link Map} holding {@link StatType}'s {@link Object2IntMap}s that
	 * hold {@link Stat} values.
	 */
	@ApiStatus.Internal
	public final Map<Identifier, Object2IntMap<Identifier>> getMap() {
		//noinspection unchecked
		return (Map<Identifier, Object2IntMap<Identifier>>)(Object) this.stats;
	}

	/**
	 * Returns the {@link Object2IntMap} holding {@link Stat} values for a given
	 * {@link StatType}.
	 * @param statType The {@link StatType}'s unique identifier.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@ApiStatus.Internal
	private final Object2IntMap<Identifier> getMap(
			@NotNull Identifier statType) throws NullPointerException
	{
		return this.stats.computeIfAbsent(requireNonNull(statType), __ -> {
			final var map = new Object2IntOpenHashMap<Identifier>();
			map.defaultReturnValue(0);
			return map;
		});
	}
	// ==================================================
	/**
	 * Returns the value of a specific {@link Stat}.
	 * @param statType The {@link StatType}'s unique identifier.
	 * @param statSubject The {@link Stat} subject's unique identifier.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public final int getValue(
			@NotNull Identifier statType, @NotNull Identifier statSubject)
			throws NullPointerException
	{
		synchronized(this.stats) {
			return getMap(statType).getInt(requireNonNull(statSubject));
		}
	}
	// --------------------------------------------------
	/**
	 * Returns the value of a specific {@link Stat}.
	 * @param type The {@link StatType}.
	 * @param subject The {@link Stat}'s subject.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @throws IllegalStateException If either the {@link StatType} or the subject is not registered.
	 */
	public final @Override <T> int getValue(
			@NotNull StatType<T> type, @NotNull T subject)
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
		return getValue(statTypeId, statSubjectId);
	}

	/**
	 * Returns the value of a specific {@link Stat}.
	 * @param stat The {@link Stat}.
	 * @throws NullPointerException If the argument is {@code null}.
	 * @throws IllegalStateException If either the corresponding {@link StatType} or the subject is not registered.
	 */
	public final @Override <T> int getValue(@NotNull Stat<T> stat)
			throws NullPointerException, IllegalStateException
	{
		return getValue(stat.getType(), stat.getValue());
	}
	// ==================================================
	/**
	 * Sets the value of a specific {@link Stat}.
	 * @param statType The {@link StatType}'s unique identifier.
	 * @param statSubject The {@link Stat} subject's unique identifier.
	 * @param value The new value to set.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public final void setValue(
			@NotNull Identifier statType, @NotNull Identifier statSubject,
			int value) throws NullPointerException
	{
		synchronized(this.stats) {
			if(value != 0) getMap(statType).put(requireNonNull(statSubject), value);
			else           getMap(statType).removeInt(statSubject);
		}
	}

	/**
	 * Sets the value of a specific {@link Stat}.
	 * @param type The {@link StatType}.
	 * @param subject The {@link Stat}'s subject.
	 * @param value The new value to set.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @throws IllegalStateException If either the {@link StatType} or the subject is not registered.
	 */
	public final <T> void setValue(
			@NotNull StatType<T> type, @NotNull T subject,
			int value) throws NullPointerException, IllegalStateException
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
		setValue(statTypeId, statSubjectId, value);
	}

	/**
	 * Sets the value of a specific {@link Stat}.
	 * @param stat The {@link Stat}.
	 * @param value The new value to set.
	 * @throws NullPointerException If the argument is {@code null}.
	 * @throws IllegalStateException If either the corresponding {@link StatType} or the subject is not registered.
	 */
	public final <T> void setValue(@NotNull Stat<T> stat, int value)
			throws NullPointerException, IllegalStateException
	{
		setValue(stat.getType(), stat.getValue(), value);
	}
	// ==================================================
	/**
	 * Adds {@link Stat} entries from another {@link StatsProvider} into this one.
	 * <p>
	 * If the other {@link StatsProvider} is an {@link McbsStats} instance,
	 * all its entries will be directly copied here. Otherwise, this method will
	 * iterate the game's registries to query all possible {@link Stat} values
	 * and copy non-zero values here.
	 * <p>
	 * This differs from {@link #setAll(StatsProvider)} in that {@code 0} values from
	 * the other {@link StatsProvider} will not overwrite existing values here.
	 * @param statsProvider The other {@link StatsProvider}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@SuppressWarnings("unchecked")
	public final void addAll(@NotNull StatsProvider statsProvider)
	{
		//argument not null check
		requireNonNull(statsProvider);
		//handle mcbs stats providers by adding all their values here
		if(statsProvider instanceof McbsStats mcbs) {
			mcbs.forEach(this::setValue);
			return;
		}
		//handle all other stat provider types by iterating game's registries
		//and querying values manually
		for(final var statType : (Registry<StatType<Object>>)(Object) BuiltInRegistries.STAT_TYPE) {
			for(final var statSubject : statType.getRegistry()) {
				//get value and put it into this mcbs stats provider
				final int value = statsProvider.getValue(statType, statSubject);
				if(value != 0) setValue(statType, statSubject, value);
			}
		}
	}

	/**
	 * Clears all existing {@link Stat} entries and adds all entries from
	 * another {@link StatsProvider}.
	 * @param statsProvider The other {@link StatsProvider}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public final void setAll(@NotNull StatsProvider statsProvider) throws NullPointerException {
		requireNonNull(statsProvider);
		clear();
		addAll(statsProvider);
	}
	// ==================================================
	/**
	 * Clears all {@link Stat} data.
	 */
	public final void clear() { this.stats.clear(); }

	/**
	 * Iterates all {@link Stat}/value entries stored in this {@link McbsStats}.
	 * Note that this does not offer entry manipulation capabilities.
	 * @param consumer The consumer that will process each entry.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public final void forEach(@NotNull EntryConsumer consumer) throws NullPointerException
	{
		requireNonNull(consumer);
		synchronized(this.stats) {
			this.stats.forEach((statType, map) -> map.forEach((statSubject, value) ->
				consumer.accept(statType, statSubject, value)));
		}
	}
	// ================================================== ==================================================
	//                                      EntryConsumer IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * A memory-efficient functional interface for consuming {@link Stat} entries
	 * stored in an {@link McbsFile} instance.
	 *
	 * @apiNote Memory efficiency is achieved by using primitive {@code int} values
	 *          instead of boxed {@link Integer} objects.
	 */
	public static @FunctionalInterface interface EntryConsumer
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
