package com.thecsdev.betterstats.mcbs.view.menubar;

import com.thecsdev.betterstats.api.client.registry.BClientRegistries;
import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorFileTab;
import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorHomepageTab;
import com.thecsdev.betterstats.api.mcbs.view.menubar.MenubarItem;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.betterstats.resources.BSSLang;
import com.thecsdev.commonmc.api.client.gui.ctxmenu.TContextMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.thecsdev.commonmc.resources.TComponent.*;

/**
 * {@link MenubarItem} implementation for "View".
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class MenubarItemView extends MenubarItem
{
	// ==================================================
	public static final MenubarItemView INSTANCE = new MenubarItemView();
	// ==================================================
	public final @Override @NotNull Component getDisplayName() { return BSSLang.gui_menubar_view(); }
	// --------------------------------------------------
	@SuppressWarnings("DataFlowIssue")
	public final @Override @NotNull TContextMenu createContextMenu(
			@NotNull Minecraft client, @NotNull McbsEditor mcbsEditor)
	{
		//not null requirements
		Objects.requireNonNull(client, "Missing 'client' instance");
		Objects.requireNonNull(mcbsEditor, "Missing 'editor' instance");

		//local-player head icon
		final @Nullable var localPlayer = client.player;
		final var localPlayerComponent = (localPlayer != null) ?
				head(localPlayer.nameAndId().id()) : air();

		//create new builder
		final var builder = new TContextMenu.Builder(client);

		//the vanilla screen button opens the vanilla stats screen
		builder.addButton(
				gui("statistics/item_picked_up").append(" ").append(BSSLang.gui_menubar_view_vanillaScreen()),
				__ -> {
					final var player = Objects.requireNonNull(client.player, "Missing 'local player' instance");
					final var screen = new StatsScreen(client.screen, player.getStats());
					client.setScreen(screen);
				});

		//home-page tab
		/*if(mcbsEditor.getCurrentTab() != McbsEditorHomepageTab.INSTANCE)
			builder.addButton(
					gui("icon/news").append(" ").append(BSSLang.gui_menubar_view_homepage()),
					__ -> mcbsEditor.addTab(McbsEditorHomepageTab.INSTANCE, true)
			);*/

		//local-player statistics tab
		if(mcbsEditor.getCurrentTab() != McbsEditorFileTab.LOCALPLAYER)
			builder.addButton(
					localPlayerComponent.append(" ").append(BSSLang.gui_menubar_view_localPlayerStats()),
					__ -> mcbsEditor.addTab(McbsEditorFileTab.LOCALPLAYER, true)
			);

		//the stats tab submenu allows switching between stats tabs
		if(mcbsEditor.getCurrentTab() instanceof McbsEditorFileTab meft) {
			builder.addSeparator();
			builder.addContextMenu(
					gui("statistics/item_used").append(" ").append(BSSLang.gui_menubar_view_statsView()),
					view_statsView(client, meft));
		}

		//build and return the context menu
		return builder.build();
	}
	// ==================================================
	/**
	 * Creates a {@link TContextMenu} that allows switching between {@link StatsView}s
	 * in a given {@link McbsEditorFileTab}.
	 * @param client The {@link Minecraft} instance the GUI belongs to.
	 * @param fileTab The target {@link McbsEditorFileTab}.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	private static final @ApiStatus.Internal TContextMenu view_statsView(
			@NotNull Minecraft client, @NotNull McbsEditorFileTab fileTab)
			throws NullPointerException
	{
		//make sure arguments aren't null for some reason
		Objects.requireNonNull(client);
		Objects.requireNonNull(fileTab);

		//create the builder
		final var builder = new TContextMenu.Builder(Objects.requireNonNull(client));
		//iterate all registered stats tabs and add a button for each
		for(final var statsViewEntry : BClientRegistries.STATS_VIEW.entrySet()) {
			final var statsView = statsViewEntry.getValue();
			builder.addButton(statsView.getDisplayName(), __ -> fileTab.setCurrentView(statsView));
		}

		//build and return the built context menu
		return builder.build();
	}
	// ==================================================
}
