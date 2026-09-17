package com.thecsdev.betterstats.mcbs.view.menubar;

import com.mojang.blaze3d.Blaze3D;
import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.api.mcbs.view.menubar.MenubarItem;
import com.thecsdev.betterstats.resource.BLanguage;
import com.thecsdev.betterstats.resource.BSprites;
import com.thecsdev.commonmc.api.client.gui.ctxmenu.TContextMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.Objects;

import static com.thecsdev.commonmc.resource.TComponent.*;
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
	public final @Override @NotNull Component getDisplayName() { return BLanguage.gui_menubar_about(); }
	// --------------------------------------------------
	@SuppressWarnings("removal")
	public final @Override @NotNull TContextMenu createContextMenu(
			@NotNull Minecraft client, @NotNull McbsEditor mcbsEditor)
	{
		Objects.requireNonNull(client, "Missing 'client' instance");
		Objects.requireNonNull(mcbsEditor, "Missing 'editor' instance");
		return new TContextMenu.Builder(Objects.requireNonNull(client))
				.addButton(
						item("item/filled_map").append(" ").append(BLanguage.gui_menubar_about_sourceCode()),
						_ -> showUriScreen(BetterStats.getProperty("mod.link.sources"), true))
				.addButton(
						head("MHF_Spider").append(" ").append(translatable("menu.reportBugs")),
						_ -> showUriScreen(BetterStats.getProperty("mod.link.issues"), true))
				.addButton(
						item("item/paper").append(" ").append(BLanguage.gui_menubar_about_legalNotices()),
						_ -> showUriScreen(BetterStats.getProperty("mod.link.legal"), true))
				.addSeparator()
				.addButton(
						gui(BSprites.gui_icon_faviconCf()).append(" ").append(literal("CurseForge")),
						_ -> showUriScreen(BetterStats.getProperty("mod.link.curseforge"), true))
				.addButton(
						gui(BSprites.gui_icon_faviconMr()).append(" ").append(literal("Modrinth")),
						_ -> showUriScreen(BetterStats.getProperty("mod.link.modrinth"), true))
				.addSeparator()
				.addButton(
						gui(BSprites.gui_icon_heart()).append(" ").append(BLanguage.gui_menubar_about_supportMe().withStyle(ChatFormatting.YELLOW)),
						_ -> showUriScreen(BetterStats.getProperty("mod.link.support_me"), true))
				.build();
	}
	// ==================================================
	/**
	 * Opens a {@link ConfirmLinkScreen} that asks the user whether
	 * they want to open the specified URI.
	 * @param uri The URI to show.
	 * @param isTrusted Whether the URI is trusted.
	 * @throws NullPointerException If the argument is {@code null}.
	 * @throws IllegalArgumentException If the URI string is not a valid {@link URI}.
	 */
	@ApiStatus.Internal
	@SuppressWarnings("SameParameterValue")
	public static final void showUriScreen(@NotNull String uri, boolean isTrusted)
			throws NullPointerException, IllegalArgumentException
	{
		//argument validity assertion
		Objects.requireNonNull(uri);
		final var toUri = URI.create(uri);

		//obtain client variables stuff
		final var client     = Objects.requireNonNull(Minecraft.getInstance());
		final var lastScreen = client.gui.screen();

		//create and set the confirmation screen
		final var screen     = new ConfirmLinkScreen(accepted -> {
			if(accepted) Blaze3D.openUri(toUri);
			client.gui.setScreen(lastScreen);
		}, toUri, isTrusted);
		client.gui.setScreen(screen);
	}
	// ==================================================
}
