package com.thecsdev.betterstats.api.mcbs.controller.tab;

import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This {@link Class} serves as a controller component in the MVC architecture.
 * Its job is to represent a "tab" in a given {@link McbsEditor}.
 * @see McbsEditor#getTabsReadOnly()
 */
public abstract class McbsEditorTab
{
	// ==================================================
	/**
	 * This value increments each time a change is made through this {@link McbsEditorTab},
	 * allowing user-intervaces (aka 'views') to known when they need to refresh.
	 * @see #getEditCount()
	 * @see #addEditCount()
	 */
	private long editCount = Long.MIN_VALUE;
	// ==================================================
	/**
	 * To prevent the {@link McbsEditor}s from opening duplicate tabs, it is
	 * essential to override this method along with {@link #equals(Object)}
	 * to enforce non-fungibility.
	 * @see Object#hashCode()
	 */
	public abstract @Override int hashCode();

	/**
	 * To prevent the {@link McbsEditor}s from opening duplicate tabs, it is
	 * essential to override this method along with {@link #hashCode()}
	 * to enforce non-fungibility.
	 * @see Object#equals(Object)
	 */
	public abstract @Override boolean equals(@Nullable Object obj);
	// ==================================================
	/**
	 * Returns the total number of edits made via this {@link McbsEditorTab}
	 * instance. This value increments each time a change occurs within this
	 * tab.
	 * <p>
	 * This can be used to track changes and determine if this tab's state
	 * has been modified since it was last checked.
	 */
	public final long getEditCount() { return this.editCount; }

	/**
	 * Increments the {@link #getEditCount()} value by {@code 1}.
	 * <p>
	 * This method is to be automatically invoked whenever a modification
	 * occurs within this tab.
	 * @see #getEditCount()
	 */
	public final void addEditCount() { this.editCount++; }
	// ==================================================
	/**
	 * This method provides the display name that will be shown in the user
	 * interface for this particular tab within an {@link McbsEditor}.
	 */
	public abstract @NotNull Component getDisplayName();
	// ==================================================
}
