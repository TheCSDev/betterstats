package com.thecsdev.betterstats.api.mcbs.model;

import com.google.gson.JsonObject;
import net.minecraft.SharedConstants;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * This {@link Class} serves as the main MVC data structure for storing and managing all
 * statistics associated with a specific player.
 */
public final class McbsFile
{
	// ==================================================
	private final @NotNull McbsStats stats = new McbsStats();
	// ==================================================
	/**
	 * Returns the {@link McbsStats} that holds statistics values.
	 */
	public final @NotNull McbsStats getStats() { return this.stats; }
	// ==================================================
	/**
	 * Serializes this {@link McbsFile} instance into a new {@link JsonObject}.
	 * @return The newly created {@link JsonObject} containing the serialized data.
	 */
	@Contract("-> new")
	public final JsonObject toJson() { return saveToJson(new JsonObject()); }

	/**
	 * Serializes this {@link McbsFile} instance into the given {@link JsonObject}.
	 * @param saveTo The {@link JsonObject} to serialize the data into.
	 * @return The same {@link JsonObject} instance that was passed as argument.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@Contract("_ -> param1")
	public final JsonObject saveToJson(@NotNull JsonObject saveTo) throws NullPointerException
	{
		//not null check
		Objects.requireNonNull(saveTo);

		//store vanilla game metadata, for compatibility with 'stats' json files
		saveTo.addProperty("DataVersion", SharedConstants.getCurrentVersion().dataVersion().version());

		//store 'stats' data
		saveTo.add("stats", getStats().toJson());

		//return the result
		return saveTo;
	}
	// --------------------------------------------------
	/**
	 * Deserializes a new {@link McbsFile} instance from the given {@link JsonObject}.
	 * @param json The {@link JsonObject} to deserialize the data from.
	 * @return A new {@link McbsFile} instance containing the deserialized data.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@Contract("_ -> new")
	public static final McbsFile fromJson(@NotNull JsonObject json) throws NullPointerException {
		Objects.requireNonNull(json);
		final var file = new McbsFile();
		file.loadFromJson(json);
		return file;
	}

	/**
	 * Deserializes data from the given {@link JsonObject} into this {@link McbsFile}
	 * instance.
	 * <p>
	 * <b>This overrides existing {@link McbsFile} data!</b>
	 * @param json The {@link JsonObject} to deserialize the data from.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public final void loadFromJson(@NotNull JsonObject json) throws NullPointerException
	{
		//not null check
		Objects.requireNonNull(json);

		//load 'stats' data
		getStats().loadFromJson(getJsonObject(json, "stats"));
	}
	// ==================================================
	/**
	 * Helper method to get a {@link JsonObject} by key, returning an empty
	 * {@link JsonObject} if the key does not exist or is not a {@link JsonObject}.
	 * @param json The parent {@link JsonObject}.
	 * @param key The key to look for.
	 */
	@ApiStatus.Internal
	static final @NotNull JsonObject getJsonObject(@NotNull JsonObject json, @NotNull String key) {
		if(!json.has(key)) return new JsonObject();
		return (json.get(key) instanceof JsonObject jobj) ? jobj : new JsonObject();
	}
	// ==================================================
}
