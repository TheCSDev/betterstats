package com.thecsdev.betterstats.api.mcbs.view.menubar;

import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.api.mcbs.view.McbsEditorGUI;
import com.thecsdev.commonmc.api.client.gui.ctxmenu.TContextMenu;
import com.thecsdev.commonmc.api.client.gui.widget.TDropdownWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a single button in {@link McbsEditorGUI}'s menubar.
 */
@Environment(EnvType.CLIENT)
public abstract class MenubarItem implements TDropdownWidget.Entry
{
	// ==================================================
	/**
	 * Returns the display name for this {@link MenubarItem}, which is shown
	 * as the label in the menubar.
	 */
	public abstract @Override @NotNull Component getDisplayName();

	/**
	 * Creates the context menu that is shown when this {@link MenubarItem}
	 * is clicked in the menubar.
	 * @param client The {@link Minecraft} instance the GUI belongs to.
	 * @param mcbsEditor The {@link McbsEditorGUI}'s {@link McbsEditor} instance.
	 */
	public abstract @NotNull TContextMenu createContextMenu(
			@NotNull Minecraft client, @NotNull McbsEditor mcbsEditor);
	// ==================================================
}
