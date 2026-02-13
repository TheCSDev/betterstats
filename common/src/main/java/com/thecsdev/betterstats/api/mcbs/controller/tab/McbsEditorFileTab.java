package com.thecsdev.betterstats.api.mcbs.controller.tab;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.api.mcbs.model.McbsFile;
import com.thecsdev.betterstats.api.mcbs.model.McbsStats;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.betterstats.resources.BSSLang;
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
import static com.thecsdev.commonmc.resources.TComponent.head;
import static net.minecraft.network.chat.Component.literal;

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
	private       @Nullable File              _lastSaveFile;
	// ==================================================
	public McbsEditorFileTab(@NotNull McbsFile mcbsFile) throws NullPointerException {
		this.mcbsFile = Objects.requireNonNull(mcbsFile);
	}
	public McbsEditorFileTab(@NotNull File file) throws NullPointerException, IOException {
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
			return head("Steve").append(" ").append(BSSLang.gui_menubar_view_localPlayerStats());
		else if(this._lastSaveFile != null)
			return head("Steve").append(" ").append(literal(this._lastSaveFile.getName()));
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
	 * @throws IOException If an I/O error occurs during {@link File} writing.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public final void saveAs(@NotNull File file) throws IOException
	{
		//not null requirements
		Objects.requireNonNull(file);
		//save to file
		final var json = this.mcbsFile.toJson();
		FileUtils.writeStringToFile(file, new Gson().toJson(json), StandardCharsets.UTF_8);
		//if successful (no io-exceptions), set the last saved file
		this._lastSaveFile = file;
	}

	/**
	 * Loads the {@link McbsFile} data from the specified file into this
	 * {@link McbsEditorFileTab}'s {@link McbsFile} instance.
	 * <p>
	 * <b>This overrides existing {@link McbsFile} data!</b>
	 * @param file The file to load the data from.
	 * @throws IOException If an I/O error occurs during {@link File} reading.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	public final void loadFrom(@NotNull File file) throws IOException
	{
		//not null requirements
		Objects.requireNonNull(file);
		//load from file
		final var json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		this.mcbsFile.loadFromJson(new Gson().fromJson(json, JsonObject.class));
		//if successful (no io-exceptions), add edit count and set the last saved file
		addEditCount();
		this._lastSaveFile = file;
	}

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
