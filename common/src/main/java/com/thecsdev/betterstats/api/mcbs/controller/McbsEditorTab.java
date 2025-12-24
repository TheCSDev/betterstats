package com.thecsdev.betterstats.api.mcbs.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.thecsdev.betterstats.api.mcbs.model.McbsFile;
import com.thecsdev.betterstats.api.mcbs.model.McbsIO;
import com.thecsdev.betterstats.api.mcbs.model.McbsStats;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.commonmc.api.client.stats.LocalPlayerStatsProvider;
import com.thecsdev.commonmc.api.stats.IStatsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils.FID_STATSVIEW;

/**
 * This {@link Class} serves as the controller component in the MVC architecture,
 * managing interactions and operations related to a specific {@link McbsFile}
 * instance.
 */
public final class McbsEditorTab
{
	// ================================================== ==================================================
	//                                      McbsEditorTab IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * "Special" {@link McbsEditorTab} instance, specifically for interfacing with
	 * {@link LocalPlayerStatsProvider} data.
	 * <p>
	 * TODO - This is maked as {@link ApiStatus.Internal} because I plan to come up
	 *        with some other mechanism for identifying and treating "special" tabs.
	 */
	@ApiStatus.Internal
	public static final McbsEditorTab LOCALPLAYER = new McbsEditorTab(new McbsFile());
	// ==================================================
	private final @NotNull McbsFile          mcbsFile;
	// --------------------------------------------------
	private final @NotNull StatsView.Filters _statFilters = new Filters();
	// --------------------------------------------------
	private long _editCount = Long.MIN_VALUE; //value increases whenever something changes
	// ==================================================
	public McbsEditorTab(@NotNull McbsFile mcbsFile) throws NullPointerException {
		this.mcbsFile = Objects.requireNonNull(mcbsFile);
	}
	public McbsEditorTab(@NotNull File file) throws NullPointerException, IOException {
		this(new McbsFile());
		loadFrom(Objects.requireNonNull(file));
	}
	// ==================================================
	//this prevents duplicate tab instances targeting the same file:
	public final @Override int hashCode() { return Objects.hash(this.mcbsFile); }
	public final @Override boolean equals(@Nullable Object obj) {
		if(obj == this) return true;
		if(obj == null || obj.getClass() != getClass()) return false;
		return Objects.equals(((McbsEditorTab)obj).mcbsFile, this.mcbsFile);
	}
	// ==================================================
	/**
	 * Returns the user-frieldly GUI display name for this {@link McbsEditorTab}.
	 */
	public final @NotNull Component getDisplayName() {
		return Component.literal(getClass().getSimpleName() + "@" + hashCode());
	}

	/**
	 * Returns an {@link IStatsProvider} view of the {@link McbsStats} instance
	 * contained within the {@link McbsFile} that this {@link McbsEditorTab} is managing.
	 * <p>
	 * <b><u>Important API note:</u></b><br>
	 * Intended to be <b>read-only</b>! Attempts to set stat values may and likely
	 * will {@code throw}!
	 */
	@ApiStatus.Experimental
	public final @NotNull IStatsProvider getStats() {
		//TODO - Return an object that's truly read-only.
		return this.mcbsFile.getStats();
	}
	// ==================================================
	/**
	 * Returns the total number of edits made to this {@link McbsEditorTab}.
	 * This value increments each time a change occurs within this tab.
	 * <p>
	 * This can be used to track changes and determine if this tab's state
	 * has been modified since it was last checked.
	 */
	public final long getEditCount() { return this._editCount; }

	/**
	 * Incrementing the {@link #getEditCount()} value.
	 * <p>
	 * This method is automatically invoked whenever a modification occurs within
	 * this tab. It is generally not necessary to call this method manually unless
	 * you have made a direct change that was not performed through this
	 * {@link McbsEditorTab} interface.
	 * @see #getEditCount()
	 */
	public final void addEditCount() { this._editCount++; }
	// ==================================================
	/**
	 * Returns the {@link StatsView} instance that is currently selected for
	 * this {@link McbsEditorTab}.
	 */
	public final @NotNull StatsView getCurrentView() {
		return this._statFilters.getProperty(StatsView.class, FID_STATSVIEW, StatsView.getDefault());
	}

	/**
	 * Sets the {@link StatsView} instance that is currently selected for
	 * this {@link McbsEditorTab}.
	 * @param view The {@link StatsView} to be set as current.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public final void setCurrentView(@NotNull StatsView view) throws NullPointerException {
		Objects.requireNonNull(view);
		this._statFilters.setProperty(StatsView.class, FID_STATSVIEW, StatsView.getDefault());
	}
	// --------------------------------------------------
	/**
	 * Returns the {@link StatsView.Filters} instance that is used for this
	 * {@link McbsEditorTab}. These filters tell {@link StatsView}s which
	 * stats are to be shown on screen.
	 */
	public final @NotNull StatsView.Filters getStatFilters() { return this._statFilters; }
	// ==================================================
	/**
	 * Saves the {@link McbsFile} data of this {@link McbsEditorTab} to the
	 * specified file.
	 * @param file The file to save the data to.
	 * @throws IOException If an I/O error occurs during file writing.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public final void saveAs(@NotNull File file) throws IOException {
		final var json = McbsIO.saveToJson(this.mcbsFile);
		FileUtils.writeStringToFile(file, new Gson().toJson(json), StandardCharsets.UTF_8);
	}

	/**
	 * Loads the {@link McbsFile} data from the specified file into this
	 * {@link McbsEditorTab}'s {@link McbsFile} instance.
	 * @param file The file to load the data from.
	 * @throws IOException If an I/O error occurs during file reading.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @apiNote This overrides existing data!
	 */
	public final void loadFrom(@NotNull File file) throws IOException {
		final var json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		McbsIO.loadFromJson(new Gson().fromJson(json, JsonObject.class), this.mcbsFile);
		addEditCount();
	}

	/**
	 * Loads the {@link McbsStats} data from the specified {@link IStatsProvider}
	 * into this {@link McbsEditorTab}'s {@link McbsFile} instance.
	 * <p>
	 * Specifically, calls {@link McbsStats#clearAndAddAll(IStatsProvider)}.
	 * @param statsProvider The {@link IStatsProvider} to load the data from.
	 * @throws NullPointerException If an argument is {@code null}.
	 * @apiNote This overrides existing data!
	 */
	public final void loadStatsFrom(@NotNull IStatsProvider statsProvider) throws NullPointerException {
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
			McbsEditorTab.this.addEditCount();
			return super.put(key, value);
		}
		public void putAll(Map<? extends Identifier, ?> m) {
			McbsEditorTab.this.addEditCount();
			super.putAll(m);
		}
		public Object putIfAbsent(Identifier key, Object value) {
			McbsEditorTab.this.addEditCount();
			return super.putIfAbsent(key, value);
		}
		// ==================================================
		public Object computeIfAbsent(Identifier key, Function<? super Identifier, ?> mappingFunction) {
			McbsEditorTab.this.addEditCount();
			return super.computeIfAbsent(key, mappingFunction);
		}
		public Object computeIfPresent(Identifier key, BiFunction<? super Identifier, ? super Object, ?> remappingFunction) {
			McbsEditorTab.this.addEditCount();
			return super.computeIfPresent(key, remappingFunction);
		}
		public Object compute(Identifier key, BiFunction<? super Identifier, ? super Object, ?> remappingFunction) {
			McbsEditorTab.this.addEditCount();
			return super.compute(key, remappingFunction);
		}
		// ==================================================
		public Object remove(Object key) {
			McbsEditorTab.this.addEditCount();
			return super.remove(key);
		}
		public boolean remove(Object key, Object value) {
			McbsEditorTab.this.addEditCount();
			return super.remove(key, value);
		}
		// ==================================================
		public boolean replace(Identifier key, Object oldValue, Object newValue) {
			McbsEditorTab.this.addEditCount();
			return super.replace(key, oldValue, newValue);
		}
		public Object replace(Identifier key, Object value) {
			McbsEditorTab.this.addEditCount();
			return super.replace(key, value);
		}
		public void replaceAll(BiFunction<? super Identifier, ? super Object, ?> function) {
			McbsEditorTab.this.addEditCount();
			super.replaceAll(function);
		}
		// ==================================================
		public Object merge(Identifier key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
			McbsEditorTab.this.addEditCount();
			return super.merge(key, value, remappingFunction);
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
