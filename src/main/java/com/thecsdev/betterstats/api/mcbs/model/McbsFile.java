package com.thecsdev.betterstats.api.mcbs.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoal;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsSivGoal;
import com.thecsdev.commonmc.api.serialization.TCodecs;
import net.minecraft.SharedConstants;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.thecsdev.commonmc.api.serialization.TCodecs.lenientListOf;

/**
 * This {@link Class} serves as the main MVC data structure for storing and managing all
 * statistics associated with a specific player.
 */
public final class McbsFile
{
	// ================================================== ==================================================
	//                                           McbsFile IMPLEMENTATION
	// ================================================== ==================================================
	private final @NotNull McbsStats      stats;
	private final @NotNull List<McbsGoal> goals;
	// ==================================================
	public McbsFile() { this(new McbsStats(), List.of()); }
	// --------------------------------------------------
	private McbsFile(
			@NotNull McbsStats statsMutable, @NotNull List<McbsGoal> goalsImmutable)
			throws NullPointerException
	{
		//field values must be independent and not associated with any other instances
		this.stats = Objects.requireNonNull(statsMutable);
		this.goals = new LinkedList<>(Objects.requireNonNull(goalsImmutable));
	}
	// ==================================================
	/**
	 * Returns the {@link McbsStats} that holds statistics values.
	 */
	public final @NotNull McbsStats getStats() { return this.stats; }

	/**
	 * Returns the {@link List} of {@link McbsGoal}s associated with
	 * this {@link McbsFile}.
	 */
	public final @NotNull List<McbsGoal> getGoals() { return this.goals; }
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
					lenientListOf(McbsGoal.CODEC).lenientOptionalFieldOf("betterstats:goals", List.of()).forGetter(mcbsFile -> (List) mcbsFile.getGoals())
			).apply(instance, (_, stats, goals) -> new McbsFile(stats, (List) goals))
	);
	// ================================================== ==================================================
}
