package com.thecsdev.betterstats.resource.dto.credits;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Represents a section in the "Credits" GUI of this mod, which
 * contains multiple {@link CreditsEntry} instances.
 */
public final class CreditsSection
{
	// ================================================== ==================================================
	//                                     CreditsSection IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link Codec} instance for serializing and deserializing
	 * {@link CreditsSection} instances.
	 */
	public static final Codec<CreditsSection> CODEC = new CodecImpl();
	// --------------------------------------------------
	private final @NotNull  Component          name;
	private final @Nullable Component          summary;
	private final @NotNull  List<CreditsEntry> entries;
	// ==================================================
	public CreditsSection(
			@NotNull  Component          name,
			@Nullable Component          summary,
			@NotNull  List<CreditsEntry> entries) throws NullPointerException
	{
		this.name    = Objects.requireNonNull(name);
		this.summary = summary;
		this.entries = Objects.requireNonNull(entries);
	}
	// ==================================================
	/**
	 * The user-friendly name of this credits section.
	 */
	public final @NotNull Component getName() { return this.name; }

	/**
	 * A brief summary or description of this credits section.
	 */
	public final @Nullable Component getSummary() { return this.summary; }

	/**
	 * A collection of {@link CreditsEntry} instances that belong to
	 * this section.
	 */
	public final @NotNull List<CreditsEntry> getEntries() { return this.entries; }
	// ================================================== ==================================================
	//                                          CodecImpl IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link Codec} implementation for serializing and deserializing
	 * {@link CreditsSection} instances.
	 */
	private static final class CodecImpl implements Codec<CreditsSection>
	{
		// ==================================================
		public final @Override <T> @NotNull DataResult<Pair<CreditsSection, T>> decode(
				@NotNull DynamicOps<T> ops, @NotNull T input)
		{
			return ops.getMap(input).flatMap(map ->
			{
				//obtain property values from the map
				final var name    = ComponentSerialization.CODEC.parse(ops, map.get("name"));
				final var summary = ComponentSerialization.CODEC.parse(ops, map.get("summary")).result();
				final var entries = CreditsEntry.CODEC.listOf().parse(ops, map.get("entries"));

				//name value is required
				if(name.error().isPresent()) //noinspection OptionalGetWithoutIsPresent
					return DataResult.error(() -> name.error().get().message());
				if(entries.error().isPresent()) //noinspection OptionalGetWithoutIsPresent
					return DataResult.error(() -> name.error().get().message());

				//construct and return result
				return DataResult.success(Pair.of(new CreditsSection(
						name.getOrThrow(),
						summary.orElse(null),
						entries.getOrThrow()
				), input));
			});
		}
		// --------------------------------------------------
		public final @Override <T> @NotNull DataResult<T> encode(
				@NotNull CreditsSection input, @NotNull DynamicOps<T> ops, @NotNull T prefix)
		{
			//use a map builder
			final var mapBuilder = ops.mapBuilder();

			//put property values in the map
			mapBuilder.add("name", ComponentSerialization.CODEC.encodeStart(ops, input.getName()));
			if(input.getSummary() != null)
				mapBuilder.add("summary", ComponentSerialization.CODEC.encodeStart(ops, input.getSummary()));
			mapBuilder.add("entries", CreditsEntry.CODEC.listOf().encodeStart(ops, input.getEntries()));

			//build and return the map
			return mapBuilder.build(prefix);
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
