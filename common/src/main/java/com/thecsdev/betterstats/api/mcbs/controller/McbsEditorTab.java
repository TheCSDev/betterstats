package com.thecsdev.betterstats.api.mcbs.controller;

import com.thecsdev.betterstats.api.mcbs.model.McbsFile;
import com.thecsdev.betterstats.api.mcbs.model.McbsStats;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	private final @NotNull McbsFile          mcbsFile;
	// ==================================================
	private final @NotNull StatsView.Filters _statFilters = new Filters();
	// --------------------------------------------------
	private long _editCount = Long.MIN_VALUE; //value increases whenever something changes
	// ==================================================
	public McbsEditorTab(@NotNull McbsFile mcbsFile) throws NullPointerException {
		this.mcbsFile = Objects.requireNonNull(mcbsFile);
	}
	// ==================================================
	public final @Override int hashCode() { return Objects.hash(this.mcbsFile); }
	public final @Override boolean equals(@Nullable Object obj) {
		if(obj == this) return true;
		if(obj == null || obj.getClass() != getClass()) return false;
		return Objects.equals(((McbsEditorTab)obj).mcbsFile, this.mcbsFile);
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
	 * Marks this {@link McbsEditorTab} as "dirty" by incrementing the edit count.
	 * <p>
	 * This method is automatically invoked whenever a modification occurs within
	 * this tab. It is generally not necessary to call this method manually unless
	 * you have made a direct change that was not performed through this
	 * {@link McbsEditorTab} interface.
	 * @see #getEditCount()
	 */
	public final void markAsDirty() { this._editCount++; }
	// ==================================================
	/**
	 * Returns an unmodifiable view of the {@link McbsStats} instance contained
	 * within the {@link McbsFile} that this {@link McbsEditorTab} is managing.
	 * @see McbsFile
	 * @see McbsFile#getStats()
	 */
	//FIXME - Make it return an immutable view. This one is currently mutable.
	public final @NotNull McbsStats getStatsReadOnly() { return this.mcbsFile.getStats(); }
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
	// ================================================== ==================================================
	//                                            Filters IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link StatsView.Filters} implementation that calls {@link #markAsDirty()}
	 * automatically whenever changes are made.
	 */
	private final @ApiStatus.Internal class Filters extends StatsView.Filters
	{
		// ==================================================
		public final @Override Object put(Identifier key, Object value) {
			McbsEditorTab.this.markAsDirty();
			return super.put(key, value);
		}
		public void putAll(Map<? extends Identifier, ?> m) {
			McbsEditorTab.this.markAsDirty();
			super.putAll(m);
		}
		public Object putIfAbsent(Identifier key, Object value) {
			McbsEditorTab.this.markAsDirty();
			return super.putIfAbsent(key, value);
		}
		// ==================================================
		public Object computeIfAbsent(Identifier key, Function<? super Identifier, ?> mappingFunction) {
			McbsEditorTab.this.markAsDirty();
			return super.computeIfAbsent(key, mappingFunction);
		}
		public Object computeIfPresent(Identifier key, BiFunction<? super Identifier, ? super Object, ?> remappingFunction) {
			McbsEditorTab.this.markAsDirty();
			return super.computeIfPresent(key, remappingFunction);
		}
		public Object compute(Identifier key, BiFunction<? super Identifier, ? super Object, ?> remappingFunction) {
			McbsEditorTab.this.markAsDirty();
			return super.compute(key, remappingFunction);
		}
		// ==================================================
		public Object remove(Object key) {
			McbsEditorTab.this.markAsDirty();
			return super.remove(key);
		}
		public boolean remove(Object key, Object value) {
			McbsEditorTab.this.markAsDirty();
			return super.remove(key, value);
		}
		// ==================================================
		public boolean replace(Identifier key, Object oldValue, Object newValue) {
			McbsEditorTab.this.markAsDirty();
			return super.replace(key, oldValue, newValue);
		}
		public Object replace(Identifier key, Object value) {
			McbsEditorTab.this.markAsDirty();
			return super.replace(key, value);
		}
		public void replaceAll(BiFunction<? super Identifier, ? super Object, ?> function) {
			McbsEditorTab.this.markAsDirty();
			super.replaceAll(function);
		}
		// ==================================================
		public Object merge(Identifier key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
			McbsEditorTab.this.markAsDirty();
			return super.merge(key, value, remappingFunction);
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
