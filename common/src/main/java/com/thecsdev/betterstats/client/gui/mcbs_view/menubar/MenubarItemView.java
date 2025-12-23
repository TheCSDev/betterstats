package com.thecsdev.betterstats.client.gui.mcbs_view.menubar;

import com.thecsdev.betterstats.api.client.registry.BClientRegistries;
import com.thecsdev.betterstats.api.mcbs.controller.McbsEditor;
import com.thecsdev.betterstats.api.mcbs.view.McbsEditorGUI;
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

import static com.thecsdev.commonmc.resources.TComponent.gui;

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
		Objects.requireNonNull(client, "Missing 'client' instance");
		Objects.requireNonNull(mcbsEditor, "Missing 'editor' instance");
		return new TContextMenu.Builder(Objects.requireNonNull(client))
				//the vanilla screen button opens the vanilla stats screen
				.addButton(gui("statistics/item_picked_up").append(" ").append(BSSLang.gui_menubar_view_vanillaScreen()), __ -> {
					final var player = Objects.requireNonNull(client.player, "Missing 'local player' instance");
					final var screen = new StatsScreen(client.screen, player.getStats());
					client.setScreen(screen);
				})
				.addSeparator()
				//the stats tab submenu allows switching between stats tabs
				.addContextMenu(
						gui("statistics/item_used").append(" ").append(BSSLang.gui_menubar_view_statsTab()),
						menu_view_tab(client, mcbsEditor))
				.build();
	}
	// ==================================================
	/**
	 * Creates a {@link TContextMenu} that allows switching between the available
	 * {@link StatsView}s.
	 * @param client The {@link Minecraft} instance the GUI belongs to.
	 * @param mcbsEditor The {@link McbsEditorGUI}'s {@link McbsEditor} instance.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	private static final @ApiStatus.Internal TContextMenu menu_view_tab(
			@NotNull Minecraft client, @NotNull McbsEditor mcbsEditor)
			throws NullPointerException
	{
		//make sure arguments aren't null for some reason
		Objects.requireNonNull(client);
		Objects.requireNonNull(mcbsEditor);

		//create the builder
		final var builder = new TContextMenu.Builder(Objects.requireNonNull(client));
		//iterate all registered stats tabs and add a button for each
		for(final var statsViewEntry : BClientRegistries.STATS_VIEW.entrySet()) {
			final var statsView = statsViewEntry.getValue();
			builder.addButton(statsView.getDisplayName(), __ -> {
				//change the editor current tab's stats view
				final @Nullable var editorTab = mcbsEditor.getCurrentTab();
				if(editorTab == null) return;
				editorTab.setCurrentView(statsView);
			});
		}

		//build and return the built context menu
		return builder.build();
	}
	// ==================================================
}
