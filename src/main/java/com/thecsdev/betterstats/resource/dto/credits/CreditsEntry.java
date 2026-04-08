package com.thecsdev.betterstats.resource.dto.credits;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.thecsdev.commonmc.api.serialization.TCodecs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Objects;

/**
 * Represents an entity that can be credited in a "Credits" GUI of this mod.
 */
public final class CreditsEntry
{
	// ================================================== ==================================================
	//                                       CreditsEntry IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link Codec} instance for serializing and deserializing
	 * {@link CreditsEntry} instances.
	 */
	public static final Codec<CreditsEntry> CODEC = new CodecImpl();
	// --------------------------------------------------
	private final @Nullable URI       avatar_uri;
	private final @NotNull  Component name;
	private final @Nullable Component summary;
	private final @Nullable URI       homepage_uri;
	// --------------------------------------------------
	private final int _hashCode;
	// ==================================================
	public CreditsEntry(
			@Nullable URI       avatar_uri,
			@NotNull  Component name,
			@Nullable Component summary,
			@Nullable URI       homepage_uri) throws NullPointerException
	{
		this.avatar_uri    = avatar_uri;
		this.name          = Objects.requireNonNull(name);
		this.summary       = summary;
		this.homepage_uri  = homepage_uri;
		this._hashCode     = Objects.hash(avatar_uri, name, summary, homepage_uri);
	}
	// ==================================================
	public int hashCode() { return this._hashCode; }
	public boolean equals(@Nullable Object obj) {
		if(this == obj) return true;
		if(obj == null || getClass() != obj.getClass()) return false;
		final var other = (CreditsEntry) obj;
		return Objects.equals(this.avatar_uri, other.avatar_uri)
				&& this.name.equals(other.name)
				&& Objects.equals(this.summary, other.summary)
				&& Objects.equals(this.homepage_uri, other.homepage_uri);
	}
	// ==================================================
	/**
	 * The {@link URI} of the "profile picture" that is associated with
	 * the credited entity.
	 */
	public final @Nullable URI getAvatarURI() { return this.avatar_uri; }

	/**
	 * User-friendly display name of the credited entity.
	 */
	public final @NotNull Component getName() { return this.name; }

	/**
	 * A short biography or description of the credited entity.
	 */
	public final @Nullable Component getSummary() { return this.summary; }

	/**
	 * The {@link URI} of the homepage or main website of the credited entity.
	 */
	public final @Nullable URI getHomepageURI() { return this.homepage_uri; }
	// ================================================== ==================================================
	//                                          CodecImpl IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link Codec} implementation for serializing and deserializing
	 * {@link CreditsEntry} instances.
	 */
	private static final class CodecImpl implements Codec<CreditsEntry>
	{
		// ==================================================
		public final @Override <T> @NotNull DataResult<Pair<CreditsEntry, T>> decode(
				@NotNull DynamicOps<T> ops, @NotNull T input)
		{
			try
			{
				return ops.getMap(input).flatMap(map ->
				{
					//obtain property values from the map
					final var avatar_uri = TCodecs.URI.parse(ops, map.get("avatar_uri")).result();
					final var name = ComponentSerialization.CODEC.parse(ops, map.get("name"));
					final var homepage_uri = TCodecs.URI.parse(ops, map.get("homepage_uri")).result();
					final var summary = ComponentSerialization.CODEC.parse(ops, map.get("summary")).result();

					//name value is required
					if (name.error().isPresent()) //noinspection OptionalGetWithoutIsPresent
						return DataResult.error(() -> name.error().get().message());

					//construct and return result
					return DataResult.success(Pair.of(new CreditsEntry(
							avatar_uri.orElse(null),
							name.getOrThrow(),
							summary.orElse(null),
							homepage_uri.orElse(null)
					), input));
				});
			}
			catch(Exception e) { return DataResult.error(() -> e.getClass() + ": " + e.getMessage()); }
		}
		// --------------------------------------------------
		public final @Override <T> @NotNull DataResult<T> encode(
				@NotNull CreditsEntry input, @NotNull DynamicOps<T> ops, @NotNull T prefix)
		{
			try
			{
				//use a map builder
				final var mapBuilder = ops.mapBuilder();

				//put property values in the map
				if (input.getAvatarURI() != null)
					mapBuilder.add("avatar_uri", TCodecs.URI.encodeStart(ops, input.getAvatarURI()));
				mapBuilder.add("name", ComponentSerialization.CODEC.encodeStart(ops, input.getName()));
				if (input.getSummary() != null)
					mapBuilder.add("summary", ComponentSerialization.CODEC.encodeStart(ops, input.getSummary()));
				if (input.getHomepageURI() != null)
					mapBuilder.add("homepage_uri", TCodecs.URI.encodeStart(ops, input.getHomepageURI()));

				//build and return the map
				return mapBuilder.build(prefix);
			}
			catch(Exception e) { return DataResult.error(() -> e.getClass() + ": " + e.getMessage()); }
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
