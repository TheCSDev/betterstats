package com.thecsdev.betterstats.api.mcbs.controller.tab;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.api.mcbs.model.McbsFile;
import com.thecsdev.betterstats.api.mcbs.model.McbsStats;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.betterstats.resource.BLanguage;
import com.thecsdev.commonmc.api.client.stats.LocalPlayerStatsProvider;
import com.thecsdev.commonmc.api.stats.IStatsProvider;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils.FID_STATSVIEW;
import static com.thecsdev.commonmc.resource.TComponent.head;
import static net.minecraft.network.chat.Component.literal;
import static org.apache.commons.io.FilenameUtils.getExtension;

/**
 * This {@link Class} serves as the controller component in the MVC architecture.
 * Its job is to represent a "tab" in a given {@link McbsEditor}, that manages
 * interactions and operations related to a specific {@link McbsFile} instance.
 */
public final class McbsEditorFileTab extends McbsEditorTab
{
	// ================================================== ==================================================
	//                                  McbsEditorFileTab IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * "Special" {@link McbsEditorTab} instance, specifically for interfacing with
	 * {@link LocalPlayerStatsProvider} data.
	 * <p>
	 * TODO - This is marked as {@link ApiStatus.Internal} because I plan to come up
	 *        with some other mechanism for identifying and treating "special" tabs.
	 */
	@ApiStatus.Internal
	public static final McbsEditorFileTab LOCALPLAYER = new McbsEditorFileTab(new McbsFile());
	// ==================================================
	private final @NotNull  McbsFile          mcbsFile;
	// --------------------------------------------------
	private final @NotNull  StatsView.Filters _statFilters = new Filters();
	private       @Nullable Path              _lastSaveFile;
	// ==================================================
	public McbsEditorFileTab(@NotNull McbsFile mcbsFile) throws NullPointerException {
		this.mcbsFile = Objects.requireNonNull(mcbsFile);
	}
	public McbsEditorFileTab(@NotNull Path file) throws NullPointerException, IOException {
		this(new McbsFile());
		loadFrom(Objects.requireNonNull(file));
	}
	// ==================================================
	//this prevents duplicate tab instances targeting the same file:
	public final @Override int hashCode() { return Objects.hash(this.mcbsFile); }
	public final @Override boolean equals(@Nullable Object obj) {
		if(obj == this) return true;
		if(obj == null || obj.getClass() != getClass()) return false;
		return Objects.equals(((McbsEditorFileTab)obj).mcbsFile, this.mcbsFile);
	}
	// ==================================================
	public final @Override @NotNull Component getDisplayName()
	{
		if(this == LOCALPLAYER)
			return head("Steve").append(" ").append(BLanguage.gui_menubar_view_localPlayerStats());
		else if(this._lastSaveFile != null)
			return head("Steve").append(" ").append(literal(this._lastSaveFile.getFileName().toString()));
		else
			return head("Steve").append(" ").append(literal(getClass().getSimpleName() + "@" + hashCode()));
	}
	// --------------------------------------------------
	/**
	 * Returns an {@link IStatsProvider} view of the {@link McbsStats} instance
	 * of the {@link McbsFile} this {@link McbsEditorFileTab} is managing.
	 * <p>
	 * <b><u>Important API note:</u></b><br>
	 * Intended to be <b>read-only</b>! Attempts to set stat values may and
	 * likely will {@code throw}!
	 */
	//TODO - Return an object that's truly read-only.
	public final @NotNull IStatsProvider getStats() { return this.mcbsFile.getStats(); }
	// ==================================================
	/**
	 * Returns the {@link StatsView} instance that is currently selected for
	 * this {@link McbsEditorFileTab}.
	 */
	public final @NotNull StatsView getCurrentView() {
		return this._statFilters.getProperty(StatsView.class, FID_STATSVIEW, StatsView.getDefault());
	}

	/**
	 * Sets the {@link StatsView} instance that is currently selected for
	 * this {@link McbsEditorFileTab}.
	 * @param view The {@link StatsView} to be set as current.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public final void setCurrentView(@NotNull StatsView view) throws NullPointerException {
		Objects.requireNonNull(view);
		this._statFilters.setProperty(StatsView.class, FID_STATSVIEW, view);
		addEditCount();
	}
	// --------------------------------------------------
	/**
	 * Returns the {@link StatsView.Filters} instance that is used for this
	 * {@link McbsEditorFileTab}. These filters tell {@link StatsView}s which
	 * stats are to be shown on screen.
	 */
	public final @NotNull StatsView.Filters getStatFilters() { return this._statFilters; }
	// ==================================================
	/**
	 * Saves the {@link McbsFile} data of this {@link McbsEditorFileTab} to the
	 * specified file.
	 * @param file The file to save the data to.
	 * @throws NullPointerException If the argument is {@code null}.
	 * @throws IllegalArgumentException If the {@link File}'s extension-name is unsupported.
	 * @throws IOException If an I/O error occurs during {@link File} writing.
	 * @apiNote Supports {@code .json} and {@code .nbt} {@link File}s only.
	 */
	public final void saveAs(@NotNull Path file)
			throws NullPointerException, IllegalArgumentException, IOException
	{
		//not null requirements
		Objects.requireNonNull(file);

		//save to file
		final var extname = getExtension(file.toString()).toLowerCase(Locale.ROOT);
		switch (extname) {
			case "json": saveAsJson(file); break;
			case "nbt": saveAsNbt(file); break;
			default: throw new IllegalArgumentException("Unsupported extname: " + extname);
		}

		//if successful (no io-exceptions), set the last saved file
		this._lastSaveFile = file;
	}

	@ApiStatus.Internal
	private final void saveAsJson(@NotNull Path file) throws IOException
	{
		try
		{
			//create parent directories if needed
			final var parent = file.getParent();
			if(parent != null) Files.createDirectories(parent);

			//encode the mcbs file to json
			final var json = McbsFile.CODEC.encodeStart(JsonOps.INSTANCE, this.mcbsFile).getOrThrow();

			//save to file
			try (final var writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
				new Gson().toJson(json, writer);
			}
		}
		catch (IOException ioe) { throw ioe; }
		catch (Exception e) { throw new IOException("Failed to save JSON file: " + file, e); }
	}

	@ApiStatus.Internal
	private final void saveAsNbt(@NotNull Path file) throws IOException
	{
		try
		{
			//create parent directories if needed
			final var parent = file.getParent();
			if(parent != null) Files.createDirectories(parent);

			//encode the mcbs file to nbt
			final var nbt = McbsFile.CODEC.encodeStart(NbtOps.INSTANCE, this.mcbsFile)
					.getOrThrow().asCompound().orElseThrow();

			//save to file
			try (final var out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(file)))) {
				NbtIo.write(nbt, out);
			}
		}
		catch (IOException ioe) { throw ioe; }
		catch (Exception e) { throw new IOException("Failed to save NBT file: " + file, e); }
	}
	// --------------------------------------------------
	/**
	 * Loads the {@link McbsFile} data from the specified file into this
	 * {@link McbsEditorFileTab}'s {@link McbsFile} instance.
	 * <p>
	 * <b>This overrides existing {@link McbsFile} data!</b>
	 * @param file The file to load the data from.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @throws IllegalArgumentException If the {@link File}'s extension-name is unsupported.
	 * @throws IOException If an I/O error occurs during {@link File} reading.
	 * @apiNote Supports {@code .json} and {@code .nbt} {@link File}s only.
	 */
	public final void loadFrom(@NotNull Path file)
			throws NullPointerException, IllegalArgumentException, IOException
	{
		//not null requirements
		Objects.requireNonNull(file);

		//load from file
		final var extname = getExtension(file.toString()).toLowerCase(Locale.ROOT);
		switch (extname) {
			case "json": loadFromJson(file); break;
			case "nbt": loadFromNbt(file); break;
			default: throw new IllegalArgumentException("Unsupported extname: " + extname);
		}

		//if successful (no io-exceptions), add edit count and set the last saved file
		addEditCount();
		this._lastSaveFile = file;
	}

	@ApiStatus.Internal
	private final void loadFromJson(@NotNull Path file) throws IOException
	{
		//check if file doesn't exist
		if(!Files.exists(file))
			throw new NoSuchFileException(file.toString());

		//read and load from file
		try (final var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8))
		{
			final var json   = JsonParser.parseReader(reader);
			final var loaded = McbsFile.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow().getFirst();
			this.mcbsFile.reloadFrom(loaded);
		}
		catch (IllegalStateException ignored) { throw new IOException("Failed to parse JSON file: " + file); }
		catch (IOException ioe) { throw ioe; }
		catch (Exception e) { throw new IOException("Failed to load JSON file: " + file, e); }
	}

	@ApiStatus.Internal
	private final void loadFromNbt(@NotNull Path file) throws IOException
	{
		try (final var in = new DataInputStream(new BufferedInputStream(Files.newInputStream(file))))
		{
			final var nbt    = NbtIo.read(in);
			final var loaded = McbsFile.CODEC.decode(NbtOps.INSTANCE, nbt).getOrThrow().getFirst();
			this.mcbsFile.reloadFrom(loaded);
		}
		catch (IllegalStateException ignored) { throw new IOException("Failed to parse NBT file: " + file); }
		catch (IOException ioe) { throw ioe; }
		catch (Exception e) { throw new IOException("Failed to load NBT file: " + file, e); }
	}
	// --------------------------------------------------
	/**
	 * Loads the {@link McbsStats} data from the specified {@link IStatsProvider}
	 * into this {@link McbsEditorFileTab}'s {@link McbsFile} instance.
	 * <p>
	 * Specifically, calls {@link McbsStats#clearAndAddAll(IStatsProvider)}.
	 * <p>
	 * <b>This overrides existing {@link McbsStats} data!</b>
	 * @param statsProvider The {@link IStatsProvider} to load the data from.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public final void loadStatsFrom(@NotNull IStatsProvider statsProvider) throws NullPointerException {
		Objects.requireNonNull(statsProvider);
		this.mcbsFile.getStats().clearAndAddAll(statsProvider);
		addEditCount();
	}
	// ================================================== ==================================================
	//                                            Filters IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link StatsView.Filters} implementation that calls {@link #addEditCount()}
	 * automatically whenever changes are made.
	 */
	private final @ApiStatus.Internal class Filters extends StatsView.Filters
	{
		// ==================================================
		public final @Override Object put(Identifier key, Object value) {
			McbsEditorFileTab.this.addEditCount();
			return super.put(key, value);
		}
		public void putAll(Map<? extends Identifier, ?> m) {
			McbsEditorFileTab.this.addEditCount();
			super.putAll(m);
		}
		public Object putIfAbsent(Identifier key, Object value) {
			McbsEditorFileTab.this.addEditCount();
			return super.putIfAbsent(key, value);
		}
		// ==================================================
		public Object computeIfAbsent(Identifier key, Function<? super Identifier, ?> mappingFunction) {
			McbsEditorFileTab.this.addEditCount();
			return super.computeIfAbsent(key, mappingFunction);
		}
		public Object computeIfPresent(Identifier key, BiFunction<? super Identifier, ? super Object, ?> remappingFunction) {
			McbsEditorFileTab.this.addEditCount();
			return super.computeIfPresent(key, remappingFunction);
		}
		public Object compute(Identifier key, BiFunction<? super Identifier, ? super Object, ?> remappingFunction) {
			McbsEditorFileTab.this.addEditCount();
			return super.compute(key, remappingFunction);
		}
		// ==================================================
		public Object remove(Object key) {
			McbsEditorFileTab.this.addEditCount();
			return super.remove(key);
		}
		public boolean remove(Object key, Object value) {
			McbsEditorFileTab.this.addEditCount();
			return super.remove(key, value);
		}
		// ==================================================
		public boolean replace(Identifier key, Object oldValue, Object newValue) {
			McbsEditorFileTab.this.addEditCount();
			return super.replace(key, oldValue, newValue);
		}
		public Object replace(Identifier key, Object value) {
			McbsEditorFileTab.this.addEditCount();
			return super.replace(key, value);
		}
		public void replaceAll(BiFunction<? super Identifier, ? super Object, ?> function) {
			McbsEditorFileTab.this.addEditCount();
			super.replaceAll(function);
		}
		// ==================================================
		public Object merge(Identifier key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
			McbsEditorFileTab.this.addEditCount();
			return super.merge(key, value, remappingFunction);
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
