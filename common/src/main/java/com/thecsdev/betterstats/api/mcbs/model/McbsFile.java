package com.thecsdev.betterstats.api.mcbs.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import org.jetbrains.annotations.NotNull;

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
	private final @NotNull McbsStats stats;
	// ==================================================
	public McbsFile() { this(new McbsStats()); }
	private McbsFile(@NotNull McbsStats stats) throws NullPointerException {
		//the stats must be truly independent and not associated with any other instances
		this.stats = Objects.requireNonNull(stats);
	}
	// ==================================================
	/**
	 * Returns the {@link McbsStats} that holds statistics values.
	 */
	public final @NotNull McbsStats getStats() { return this.stats; }
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
	public static final Codec<McbsFile> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					Codec.INT.fieldOf("DataVersion").forGetter(file -> SharedConstants.getCurrentVersion().dataVersion().version()),
					McbsStats.CODEC.fieldOf("stats").forGetter(McbsFile::getStats)
			).apply(instance, (dataVersion, stats) -> new McbsFile(stats))
	);
	// ================================================== ==================================================
}
