package com.thecsdev.betterstats.api.mcbs;

import com.google.gson.JsonObject;
import io.netty.util.internal.UnstableApi;
import net.minecraft.IdentifierException;
import net.minecraft.SharedConstants;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Utility methods for saving and loading {@link McbsFile} instances.
 */
@UnstableApi
@ApiStatus.Experimental
public final class McbsIO
{
	// ==================================================
	private McbsIO() {}
	// ==================================================
	/**
	 * Serializes an {@link McbsFile}'s data to a new {@link JsonObject} instance and then
	 * returns that {@link JsonObject}.
	 * @param mcbsFile The {@link McbsFile} to serialize/save.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@Contract("_ -> new")
	public static final @NotNull JsonObject saveToJson(@NotNull McbsFile mcbsFile) throws NullPointerException {
		return saveToJson(mcbsFile, new JsonObject());
	}

	/**
	 * Serializes an {@link McbsFile}'s data to the given {@link JsonObject} instance and then
	 * returns that same {@link JsonObject}.
	 * @param mcbsFile The {@link McbsFile} to serialize/save.
	 * @param json The {@link JsonObject} to save the data into.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	@Contract("_, _ -> param2")
	public static final @NotNull JsonObject saveToJson(
			@NotNull McbsFile mcbsFile, @NotNull JsonObject json) throws NullPointerException
	{
		//not null check
		Objects.requireNonNull(mcbsFile);
		Objects.requireNonNull(json);

		//store vanilla game metadata, for compatibility with 'stats' json files
		json.addProperty("DataVersion", SharedConstants.getCurrentVersion().dataVersion().version());

		//store 'stats' data
		final var json_stats = new JsonObject();
		json.add("stats", json_stats);
		for(final var statTypeEntry : mcbsFile.getStats().getMap().entrySet())
		{
			//create the json object for a given stat-type
			final var json_stats_type = new JsonObject();
			json_stats.add(statTypeEntry.getKey().toString(), json_stats_type);

			//iterate each stat, and store it in the stat-type's json
			for(final var statEntry : statTypeEntry.getValue().object2IntEntrySet()) {
				if(statEntry.getIntValue() != 0) //do not waste storage with zeros
					json_stats_type.addProperty(statEntry.getKey().toString(), statEntry.getIntValue());
			}
		}

		//return the result
		return json;
	}
	// --------------------------------------------------
	/**
	 * Deserializes an {@link McbsFile}'s data and returns a new {@link McbsFile} instance
	 * containing that data.
	 * @param json The serialized {@link McbsFile} data.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@Contract("_ -> new")
	public static final @NotNull McbsFile loadFromJson(@NotNull JsonObject json) throws NullPointerException {
		return loadFromJson(json, new McbsFile());
	}

	/**
	 * Deserializes an {@link McbsFile}'s data and loads it to the given {@link McbsFile} instance.
	 * @param json The serialized {@link McbsFile} data.
	 * @param mcbsFile The {@link McbsFile} instance to load the data into.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	@Contract("_, _ -> param2")
	public static final @NotNull McbsFile loadFromJson(
			@NotNull JsonObject json, @NotNull McbsFile mcbsFile) throws NullPointerException
	{
		//not null check
		Objects.requireNonNull(json);
		Objects.requireNonNull(mcbsFile);

		//clear existing statistics
		mcbsFile.getStats().clear();

		//get the statistics json object
		final @Nullable var json_stats = getJsonObject(json, "stats");
		if(json_stats != null)
			for(final var statTypeEntry : json_stats.entrySet())
		{
			//obtain json-object
			final @Nullable var json_stats_type = getJsonObject(json_stats, statTypeEntry.getKey());
			if(json_stats_type == null) continue;

			//construct stat-type id
			@NotNull Identifier statTypeId;
			try { statTypeId = Identifier.parse(statTypeEntry.getKey()); }
			catch(IdentifierException e) { continue; }

			//iterate all stat values
			for(final var statEntry : json_stats_type.entrySet())
			{
				//ensure stat value is a number
				if(!statEntry.getValue().isJsonPrimitive() ||
						!statEntry.getValue().getAsJsonPrimitive().isNumber())
					continue;

				//construct stat id
				@NotNull Identifier statId;
				try { statId = Identifier.parse(statEntry.getKey()); }
				catch(IdentifierException e) {  continue; }

				//finally, set the [stat-type / stat-subject] value in the mcbs file
				mcbsFile.getStats().setValue(
						statTypeId, statId,
						statEntry.getValue().getAsInt());
			}
		}

		//return the result
		return mcbsFile;
	}
	// ==================================================
	/**
	 * Helper method to get a {@link JsonObject} by key, or {@code null} if it doesn't exist or
	 * isn't a {@link JsonObject}.
	 * @param json The parent {@link JsonObject}.
	 * @param key The key to look for.
	 */
	@ApiStatus.Internal
	private static final @Nullable JsonObject getJsonObject(@NotNull JsonObject json, @NotNull String key) {
		if(!json.has(key)) return null;
		return (json.get(key) instanceof JsonObject jobj) ? jobj : null;
	}
	// ==================================================
}
