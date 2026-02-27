package com.thecsdev.betterstats.mcbs.view.menubar;

import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorFileTab;
import com.thecsdev.betterstats.api.mcbs.view.menubar.MenubarItem;
import com.thecsdev.betterstats.resource.BLanguage;
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

import static com.thecsdev.commonmc.resource.TComponent.*;

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
	public final @Override @NotNull Component getDisplayName() { return BLanguage.gui_menubar_view(); }
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
				gui("statistics/item_picked_up").append(" ").append(BLanguage.gui_menubar_view_vanillaScreen()),
				__ -> {
					final var player = Objects.requireNonNull(client.player, "Missing 'local player' instance");
					final var screen = new StatsScreen(client.screen, player.getStats());
					client.setScreen(screen);
				});
		builder.addSeparator();

		//home-page tab
		//FIXME - Implement homepage soon
		/*builder.addButton(
				gui("icon/news").append(" ").append(BLanguage.gui_menubar_view_homepage()),
				__ -> mcbsEditor.addTab(McbsEditorHomepageTab.INSTANCE, true)
		);*/

		//local-player statistics tab
		builder.addButton(
				localPlayerComponent.append(" ").append(BLanguage.gui_menubar_view_localPlayerStats()),
				__ -> mcbsEditor.addTab(McbsEditorFileTab.LOCALPLAYER, true)
		);

		//build and return the context menu
		return builder.build();
	}
	// ==================================================
}
