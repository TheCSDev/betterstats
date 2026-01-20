package com.thecsdev.betterstats.api.mcbs.controller;

import com.thecsdev.betterstats.api.client.gui.screen.BetterStatsScreen;
import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorFileTab;
import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorHomepageTab;
import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorNullTab;
import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorTab;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.util.Collections.unmodifiableSet;

/**
 * This {@link Class} functions as the central controller in the MVC architecture,
 * overseeing the complete editor interface. It coordinates and manages all
 * {@link McbsEditorTab} instances contained within the editor, effectively
 * representing the entire graphical user interface (GUI).
 */
public final class McbsEditor
{
	// ==================================================
	/**
	 * Main {@link McbsEditor} instance used by {@link BetterStatsScreen}.
	 */
	public static final McbsEditor INSTANCE = new McbsEditor();
	// ==================================================
	private final @NotNull Set<McbsEditorTab> _tabs          = new LinkedHashSet<>();
	private final @NotNull Set<McbsEditorTab> _tabsImmutable = unmodifiableSet(this._tabs);
	private       @NotNull McbsEditorTab      _currentTab    = McbsEditorNullTab.INSTANCE;
	// --------------------------------------------------
	/**
	 * This value increments each time a change is through this {@link McbsEditor},
	 * allowing user-intervaces (aka 'views') to known when they need to refresh.
	 * @see #getEditCount()
	 * @see #addEditCount()
	 */
	private long _editCount = Long.MIN_VALUE;
	// ==================================================
	public McbsEditor() {
		//addTab(McbsEditorHomepageTab.INSTANCE, true);
		addTab(McbsEditorFileTab.LOCALPLAYER, true);
	}
	// ==================================================
	public final @Override int hashCode() { return super.hashCode(); }
	public final @Override boolean equals(Object obj) { return super.equals(obj); }
	// ==================================================
	/**
	 * Returns the total number of edits made via {@link McbsEditor}.
	 * This value increments each time a change occurs within this editor,
	 * such as adding/removing tabs or modifying tab content.
	 * <p>
	 * This can be used to track changes and determine if this editor's state
	 * has been modified since it was last checked.
	 */
	public final long getEditCount() { return this._editCount; }

	/**
	 * Incrementing the {@link #getEditCount()} value.
	 * <p>
	 * This method is automatically invoked whenever a modification occurs within
	 * this editor. It is generally not necessary to call this method manually
	 * unless you have made a direct change that was not performed through this
	 * {@link McbsEditor} interface.
	 * @see #getEditCount()
	 */
	public final void addEditCount() { this._editCount++; }
	// ==================================================
	/**
	 * Returns an unmodifiable {@link Collection} containing all the {@link McbsEditorTab}
	 * instances that are currently open within this editor.
	 * <p>
	 * To add or remove {@link McbsEditorTab}s, please refer to "See Also".
	 * @see Collections#unmodifiableSet(Set)
	 * @see #addTab(McbsEditorTab, boolean)
	 * @see #removeTab(McbsEditorTab)
	 */
	public final Collection<McbsEditorTab> getTabsReadOnly() { return this._tabsImmutable; }

	/**
	 * Attempts to add the specified {@link McbsEditorTab} within this editor.
	 *
	 * @param tab The {@link McbsEditorTab} to be opened.
	 * @param setAsCurrent If {@code true}, the newly added tab will be set as the current tab.
	 * @return {@code true} if the tab was successfully opened; {@code false} if
	 * the tab could not be added.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public final boolean addTab(@NotNull McbsEditorTab tab, boolean setAsCurrent)
			throws NullPointerException
	{
		Objects.requireNonNull(tab);
		//add the tab, and increment edit count only if added
		final var result = this._tabs.add(tab);
		if(result) addEditCount();
		//set current tab if needed. this can also increment edit count on its own
		if(setAsCurrent || getCurrentTab() == McbsEditorNullTab.INSTANCE)
			setCurrentTab(tab);
		//return the result
		return result;
	}

	/**
	 * Attempts to remove the specified {@link McbsEditorTab} from this editor.
	 * @param tab The {@link McbsEditorTab} to be removed.
	 * @return {@code true} if the tab was successfully removed; {@code false} if
	 *         the tab could not be removed.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public final boolean removeTab(@NotNull McbsEditorTab tab) throws NullPointerException
	{
		Objects.requireNonNull(tab);
		//attempt to remove, return false if removing fails
		if(!this._tabs.remove(tab)) return false;
		//if the removed tab was the current tab, clear the current tab
		if(tab == this._currentTab) this._currentTab = McbsEditorNullTab.INSTANCE;
		//mark as dirty and return
		addEditCount();
		return true;
	}
	// --------------------------------------------------
	/**
	 * Returns the current {@link McbsEditorTab} being edited by this {@link McbsEditor}.
	 */
	//FIXME - May wanna make this NotNull to avoid confusion with the null tab
	public final @NotNull McbsEditorTab getCurrentTab() { return this._currentTab; }

	/**
	 * Sets the current {@link McbsEditorTab} being edited by this {@link McbsEditor}.
	 * @param tab The {@link McbsEditorTab} to set as the current tab. Must be
	 *            contained by {@link #getTabsReadOnly()}.
	 */
	public final void setCurrentTab(@Nullable McbsEditorTab tab)
	{
		//cannot set if already set
		if(tab == this._currentTab) return;
		//use 'null' tab where appropriate
		else if(tab == null || !this._tabs.contains(tab))
			tab = McbsEditorNullTab.INSTANCE;
		//set the tab and mark as dirty
		this._currentTab = tab;
		addEditCount();
	}
	// ==================================================
}
