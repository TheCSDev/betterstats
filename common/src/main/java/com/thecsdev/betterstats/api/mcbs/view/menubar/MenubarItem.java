package com.thecsdev.betterstats.api.mcbs.view.menubar;

import com.thecsdev.betterstats.api.client.registry.BClientRegistries;
import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.api.mcbs.view.McbsEditorGUI;
import com.thecsdev.betterstats.client.gui.mcbs_view.menubar.BMenubarItemAbout;
import com.thecsdev.betterstats.client.gui.mcbs_view.menubar.BMenubarItemFile;
import com.thecsdev.betterstats.client.gui.mcbs_view.menubar.BMenubarItemView;
import com.thecsdev.commonmc.api.client.gui.ctxmenu.TContextMenu;
import com.thecsdev.commonmc.api.client.gui.widget.TDropdownWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import static com.thecsdev.betterstats.BetterStats.MOD_ID;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

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
	/**
	 * Registers internal {@link MenubarItem} instances.
	 */
	public static final @ApiStatus.Internal void bootstrap() {
		BClientRegistries.MENUBAR_ITEM.put(fromNamespaceAndPath(MOD_ID, "file"), BMenubarItemFile.INSTANCE);
		BClientRegistries.MENUBAR_ITEM.put(fromNamespaceAndPath(MOD_ID, "view"), BMenubarItemView.INSTANCE);
		BClientRegistries.MENUBAR_ITEM.put(fromNamespaceAndPath(MOD_ID, "about"), BMenubarItemAbout.INSTANCE);
	}
	// ==================================================
}
