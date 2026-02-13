package com.thecsdev.betterstats.api.mcbs.controller.tab;

import com.thecsdev.betterstats.resources.BSSLang;
import com.thecsdev.betterstats.resources.BSSSprites;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.thecsdev.commonmc.resources.TComponent.gui;

/**
 * {@link McbsEditorTab} implementation for displaying the "Settings" GUI for
 * configuring this mod.
 */
public final class McbsEditorSettingsTab extends McbsEditorTab
{
	// ==================================================
	/**
	 * Main singleton instance of this class.
	 */
	public static final McbsEditorSettingsTab INSTANCE = new McbsEditorSettingsTab();
	// ==================================================
	private McbsEditorSettingsTab() {}
	// ==================================================
	public final @Override int hashCode() { return System.identityHashCode(this); }
	public final @Override boolean equals(@Nullable Object obj) { return this == obj; }
	// ==================================================
	public final @Override @NotNull Component getDisplayName() {
		return gui(BSSSprites.gui_icon_settings()).append(" ").append(BSSLang.gui_menubar_file_settings());
	}
	// ==================================================
}
