package com.thecsdev.betterstats.api.mcbs.view.menubar;

import com.thecsdev.betterstats.api.client.gui.screen.BetterStatsConfigScreen;
import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.resources.BSSLang;
import com.thecsdev.betterstats.resources.BSSSprites;
import com.thecsdev.commonmc.api.client.gui.ctxmenu.TContextMenu;
import com.thecsdev.commonmc.resources.TCDCSprites;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.thecsdev.commonmc.resources.TComponent.air;
import static com.thecsdev.commonmc.resources.TComponent.gui;

/*
 * [File] [View] [About]
 *   ^
 */
final class MiFile extends MenubarItem
{
	// ==================================================
	public static final MiFile INSTANCE = new MiFile();
	// ==================================================
	public final @Override @NotNull Component getDisplayName() { return BSSLang.gui_menubar_file(); }
	// --------------------------------------------------
	public final @Override @NotNull TContextMenu createContextMenu(
			@NotNull Minecraft client, @NotNull McbsEditor mcbsEditor)
	{
		return new TContextMenu.Builder(client)
				.addButton(
						gui(TCDCSprites.gui_icon_fsFolder()).append(" ").append(BSSLang.gui_menubar_file_open()),
						__ -> { throw new NotImplementedException("I forgot to implement this"); })
				.addButton(
						air().append(" ").append(BSSLang.gui_menubar_file_saveAs()),
						__ -> { throw new NotImplementedException("I forgot to implement this"); })
				.addSeparator()
				.addButton(
						gui(BSSSprites.gui_icon_settings()).append(" ").append(BSSLang.gui_menubar_file_settings()),
						__ -> {
							final var cs = new BetterStatsConfigScreen(client.screen);
							client.setScreen(cs.getAsScreen());
						})
				.addButton(
						gui(BSSSprites.gui_icon_close()).append(" ").append(BSSLang.gui_menubar_file_close()),
						__ -> Optional.ofNullable(client.screen).ifPresent(Screen::onClose))
				.build();
	}
	// ==================================================
}
