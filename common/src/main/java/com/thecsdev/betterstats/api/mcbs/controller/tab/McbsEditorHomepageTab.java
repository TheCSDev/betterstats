package com.thecsdev.betterstats.api.mcbs.controller.tab;

import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.resources.BSSLang;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The main "homepage" tab that is selected by default when an {@link McbsEditor}
 * instance is created. This is the "entrypoint" tab.
 */
@ApiStatus.Experimental
public final class McbsEditorHomepageTab extends McbsEditorTab
{
	// ==================================================
	public static final McbsEditorHomepageTab INSTANCE = new McbsEditorHomepageTab();
	// ==================================================
	private McbsEditorHomepageTab() {}
	// ==================================================
	public final @Override int hashCode() { return System.identityHashCode(this); }
	public final @Override boolean equals(@Nullable Object obj) { return this == obj; }
	// ==================================================
	public final @Override @NotNull Component getDisplayName() {
		return BSSLang.gui_menubar_view_homepage();
	}
	// ==================================================
}
