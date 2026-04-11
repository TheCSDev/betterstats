package com.thecsdev.betterstats.api.mcbs.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoal;
import com.thecsdev.commonmc.api.serialization.TCodec;
import net.minecraft.SharedConstants;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This {@link Class} serves as the main MVC data structure for storing and managing all
 * statistics associated with a specific player.
 */
public final class McbsFile
{
	// ================================================== ==================================================
	//                                           McbsFile IMPLEMENTATION
	// ================================================== ==================================================
	private final @NotNull McbsStats                 stats;
	private final @NotNull Map<Identifier, McbsGoal> goals;
	// ==================================================
	public McbsFile() { this(new McbsStats(), Map.of()); }
	// --------------------------------------------------
	private McbsFile(
			@NotNull McbsStats statsMutable, @NotNull Map<Identifier, McbsGoal> goalsImmutable)
			throws NullPointerException
	{
		//field values must be independent and not associated with any other instances
		this.stats = Objects.requireNonNull(statsMutable);
		this.goals = new LinkedHashMap<>(Objects.requireNonNull(goalsImmutable));
	}
	// ==================================================
	/**
	 * Returns the {@link McbsStats} that holds statistics values.
	 */
	public final @NotNull McbsStats getStats() { return this.stats; }

	/**
	 * Returns the {@link McbsGoal}s associated with this {@link McbsFile}.
	 */
	public final @NotNull Map<Identifier, McbsGoal> getGoals() { return this.goals; }
	// ==================================================
	/**
	 * This method completely replaces all the data in this {@link McbsFile} with data
	 * copied from the provided {@link McbsFile}.
	 */
	public final void reloadFrom(@NotNull McbsFile mcbsFile) throws NullPointerException {
		Objects.requireNonNull(mcbsFile);
		if(mcbsFile == this) return;
		getStats().clearAndAddAll(mcbsFile.getStats());
	}
	// ================================================== ==================================================
	//                                              Codec IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link Codec} implementation for {@link McbsFile}.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static final Codec<McbsFile> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					Codec.INT.fieldOf("DataVersion").forGetter(_ -> SharedConstants.getCurrentVersion().dataVersion().version()),
					McbsStats.CODEC.fieldOf("stats").forGetter(McbsFile::getStats),
					TCodec.lenientMap(Identifier.CODEC, McbsGoal.CODEC).lenientOptionalFieldOf("betterstats:goals", Map.of()).forGetter(mcbsFile -> (Map) mcbsFile.getGoals())
			).apply(instance, (_, stats, goals) -> new McbsFile(stats, (Map) goals))
	);
	// ================================================== ==================================================
}
