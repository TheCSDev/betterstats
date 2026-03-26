package com.thecsdev.betterstats.api.mcbs.controller.tab;

import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Placeholder {@link McbsEditorTab} implementation used for when there are no
 * selected tabs in a given {@link McbsEditor}.
 */
@ApiStatus.Internal
public final class McbsEditorNullTab extends McbsEditorTab
{
	// ==================================================
	/**
	 * Main singleton instance of this class.
	 */
	public static final McbsEditorNullTab INSTANCE = new McbsEditorNullTab();
	// ==================================================
	private McbsEditorNullTab() {}
	// ==================================================
	public final @Override int hashCode() { return System.identityHashCode(this); }
	public final @Override boolean equals(@Nullable Object obj) { return this == obj; }
	// ==================================================
	public final @Override @NotNull Component getDisplayName() {
		return Component.literal("null");
	}
	// ==================================================
}
