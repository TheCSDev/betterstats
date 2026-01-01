package com.thecsdev.betterstats.api.mcbs.model;

import com.google.gson.JsonObject;
import com.thecsdev.commonmc.api.stats.IStatsProvider;
import net.minecraft.IdentifierException;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.thecsdev.betterstats.api.mcbs.model.McbsFile.getJsonObject;

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
	McbsStats() {} //cannot be instantiated by outsiders. is bound to its corresponding file
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
	// ==================================================
	/**
	 * Serializes this {@link McbsStats} instance into a new {@link JsonObject}.
	 * @return The newly created {@link JsonObject} containing the serialized data.
	 */
	@Contract("-> new")
	public final JsonObject toJson() { return saveToJson(new JsonObject()); }

	/**
	 * Serializes this {@link McbsStats} instance into a given {@link JsonObject}.
	 * @param saveTo The {@link JsonObject} to save the data into.
	 * @return The same {@link JsonObject} instance that was passed as argument.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@Contract("_ -> param1")
	public final JsonObject saveToJson(@NotNull JsonObject saveTo) throws NullPointerException
	{
		//not null requirement
		Objects.requireNonNull(saveTo);

		//iterate stat-types and store statistics for each
		for(final var statTypeEntry : getIntValues().entrySet())
		{
			//create the json object for a given stat-type
			final var json_statType = new JsonObject();
			saveTo.add(statTypeEntry.getKey().toString(), json_statType);

			//iterate each stat, and store it in the stat-type's json
			for(final var statEntry : statTypeEntry.getValue().entrySet()) {
				if(statEntry.getValue() != 0) //do not waste storage with zeros
					json_statType.addProperty(statEntry.getKey().toString(), statEntry.getValue());
			}
		}
		return saveTo;
	}
	// --------------------------------------------------
	//can't have "#fromJson(JsonObject)" because the constructor is restricted

	/**
	 * Deserializes data from a given {@link JsonObject} into this {@link McbsStats} instance.
	 * <p>
	 * <b>This overrides existing {@link McbsStats} data!</b>
	 * @param json The serialized {@link McbsStats} data.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public final void loadFromJson(@NotNull JsonObject json) throws NullPointerException
	{
		//not null requirement, and then clear existing data
		Objects.requireNonNull(json);
		clear();

		//iterate stat-types and load statistics for each one
		for(final var statTypeEntry : json.entrySet())
		{
			//obtain json-object
			final @Nullable var json_statType = getJsonObject(json, statTypeEntry.getKey());
			if(json_statType.isEmpty()) continue;

			//construct stat-type id
			@NotNull Identifier statTypeId;
			try { statTypeId = Identifier.parse(statTypeEntry.getKey()); }
			catch(IdentifierException e) { continue; }

			//iterate all stat values
			for(final var statEntry : json_statType.entrySet())
			{
				//ensure stat value is a number
				if(!statEntry.getValue().isJsonPrimitive() ||
						!statEntry.getValue().getAsJsonPrimitive().isNumber())
					continue;

				//construct stat id
				@NotNull Identifier statId;
				try { statId = Identifier.parse(statEntry.getKey()); }
				catch(IdentifierException e) { continue; }

				//finally, set the [stat-type / stat-subject] value in the mcbs file
				setIntValue(statTypeId, statId, statEntry.getValue().getAsInt());
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
