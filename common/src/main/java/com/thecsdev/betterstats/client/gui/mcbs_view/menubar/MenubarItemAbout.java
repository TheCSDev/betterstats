package com.thecsdev.betterstats.client.gui.mcbs_view.menubar;

import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.api.mcbs.view.menubar.MenubarItem;
import com.thecsdev.betterstats.resources.BSSLang;
import com.thecsdev.betterstats.resources.BSSSprites;
import com.thecsdev.commonmc.api.client.gui.ctxmenu.TContextMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.thecsdev.commonmc.resources.TComponent.*;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;

/**
 * {@link MenubarItem} implementation for "About".
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class MenubarItemAbout extends MenubarItem
{
	// ==================================================
	public static final MenubarItemAbout INSTANCE = new MenubarItemAbout();
	// ==================================================
	public final @Override @NotNull Component getDisplayName() { return BSSLang.gui_menubar_about(); }
	// --------------------------------------------------
	@SuppressWarnings("removal")
	public final @Override @NotNull TContextMenu createContextMenu(
			@NotNull Minecraft client, @NotNull McbsEditor mcbsEditor)
	{
		return new TContextMenu.Builder(Objects.requireNonNull(client))
				.addButton(
						item("item/filled_map").append(" ").append(BSSLang.gui_menubar_about_sourceCode()),
						__ -> showUrlScreen(BetterStats.getProperty("mod.link.sources"), true))
				.addButton(
						head("MHF_Spider").append(" ").append(translatable("menu.reportBugs")),
						__ -> showUrlScreen(BetterStats.getProperty("mod.link.issues"), true))
				.addSeparator()
				.addButton(
						gui(BSSSprites.gui_icon_faviconCf()).append(" ").append(literal("CurseForge")),
						__ -> showUrlScreen(BetterStats.getProperty("mod.link.curseforge"), true))
				.addButton(
						gui(BSSSprites.gui_icon_faviconMr()).append(" ").append(literal("Modrinth")),
						__ -> showUrlScreen(BetterStats.getProperty("mod.link.modrinth"), true))
				.addSeparator()
				.addButton(
						gui(BSSSprites.gui_icon_heart()).append(" ").append(BSSLang.gui_menubar_about_supportMe().withStyle(ChatFormatting.YELLOW)),
						__ -> showUrlScreen(BetterStats.getProperty("mod.link.support_me"), true))
				.build();
	}
	// ==================================================
	/**
	 * Opens a {@link ConfirmLinkScreen} that asks the user whether
	 * they want to open the specified URI.
	 * @param uri The URI to show.
	 * @param isTrusted Whether the URI is trusted.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@SuppressWarnings("SameParameterValue")
	private static final void showUrlScreen(@NotNull String uri, boolean isTrusted)
			throws NullPointerException
	{
		//argument validity assertion
		Objects.requireNonNull(uri);

		//obtain client variables stuff
		final var client     = Objects.requireNonNull(Minecraft.getInstance());
		final var lastScreen = client.screen;

		//create and set the confirmation screen
		final var screen     = new ConfirmLinkScreen(accepted -> {
			if(accepted) Util.getPlatform().openUri(uri);
			client.setScreen(lastScreen);
		}, uri, isTrusted);
		client.setScreen(screen);
	}
	// ==================================================
}
