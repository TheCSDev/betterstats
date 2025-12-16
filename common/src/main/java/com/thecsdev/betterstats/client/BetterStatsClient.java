package com.thecsdev.betterstats.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.api.client.gui.screen.BetterStatsScreen;
import com.thecsdev.betterstats.api.client.gui.screen.IBetterStatsGui;
import com.thecsdev.betterstats.api.client.gui.statstab.StatsTab;
import com.thecsdev.betterstats.api.mcbs.McbsFile;
import com.thecsdev.betterstats.api.mcbs.McbsIO;
import com.thecsdev.betterstats.client.gui.statstab.*;
import com.thecsdev.betterstats.resources.BSSLang;
import com.thecsdev.betterstats.resources.BSSSprites;
import com.thecsdev.common.util.TUtils;
import com.thecsdev.commonmc.api.client.gui.ctxmenu.TContextMenu;
import com.thecsdev.commonmc.api.client.gui.screen.TFileChooserScreen;
import com.thecsdev.commonmc.api.client.gui.util.TGuiUtils;
import com.thecsdev.commonmc.resources.TCDCSprites;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

import static com.thecsdev.betterstats.api.client.registry.BClientRegistries.MENUBAR_BUTTON;
import static com.thecsdev.betterstats.api.client.registry.BClientRegistries.STATS_TAB;
import static com.thecsdev.commonmc.api.client.hooks.GuiHooks.registerVanillaButtonMod;
import static com.thecsdev.commonmc.resources.TComponent.*;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

/**
 * The main "client" entry-point for this mod, that is executed
 * by all loaders (fabric/neoforge).
 */
public class BetterStatsClient extends BetterStats
{
	// ==================================================
	public BetterStatsClient()
	{
		//register statistics tabs
		STATS_TAB.put(fromNamespaceAndPath(MOD_ID, "general"), StatsTabGeneral.INSTANCE);
		STATS_TAB.put(fromNamespaceAndPath(MOD_ID, "items"), StatsTabItems.INSTANCE);
		STATS_TAB.put(fromNamespaceAndPath(MOD_ID, "mobs"), StatsTabMobs.INSTANCE);
		STATS_TAB.put(fromNamespaceAndPath(MOD_ID, "food"), StatsTabFood.INSTANCE);
		STATS_TAB.put(fromNamespaceAndPath(MOD_ID, "hunter"), StatsTabHunter.INSTANCE);

		//register menubar buttons
		MENUBAR_BUTTON.put(fromNamespaceAndPath(MOD_ID, "file"), Pair.of(BSSLang.gui_menubar_file(), BetterStatsClient::menu_file));
		MENUBAR_BUTTON.put(fromNamespaceAndPath(MOD_ID, "view"), Pair.of(BSSLang.gui_menubar_view(), BetterStatsClient::menu_view));
		MENUBAR_BUTTON.put(fromNamespaceAndPath(MOD_ID, "about"), Pair.of(BSSLang.gui_menubar_about(), BetterStatsClient::menu_about));

		//modify the "Statistics" button on the game's pause screen
		registerVanillaButtonMod(PauseScreen.class, translatable("gui.stats"), (button, vanillaOnClick) -> {
			//if the user is holding down "Shift", run vanilla button functionality
			if(TGuiUtils.isShiftDown()) vanillaOnClick.run();
			//else open the Better Statistics Screen
			else {
				final var client = Minecraft.getInstance();
				client.setScreen(new BetterStatsScreen(client.screen).getAsScreen());
			}
		});
	}
	// ==================================================
	/**
	 * Creates a {@link TContextMenu} for the "File" menubar button.
	 * @param gui The {@link IBetterStatsGui} instance.
	 */
	@ApiStatus.Internal
	private static final TContextMenu menu_file(@NotNull IBetterStatsGui gui)
	{
		return new TContextMenu.Builder(Objects.requireNonNull(gui.getClient()))
				.addButton(
						gui(TCDCSprites.gui_icon_fsFolder()).append(" ").append(BSSLang.gui_menubar_file_open()),
						__ -> showOpenFileDialog(gui))
				.addButton(
						air().append(" ").append(BSSLang.gui_menubar_file_saveAs()),
						__ -> showSaveFileDialog(gui))
				.addSeparator()
				.addButton(
						StatsTabSettings.INSTANCE.getDisplayName(),
						__ -> { gui.setStatsTab(StatsTabSettings.INSTANCE); gui.refresh(); })
				.addButton(
						gui(BSSSprites.gui_icon_close()).append(" ").append(BSSLang.gui_menubar_file_close()),
						__ -> Optional.ofNullable(gui.getClient().screen).ifPresent(Screen::onClose))
				.build();
	}
	// --------------------------------------------------
	/**
	 * Creates a {@link TContextMenu} for the "View" menubar button.
	 * @param gui The {@link IBetterStatsGui} instance.
	 */
	@ApiStatus.Internal
	private static final TContextMenu menu_view(@NotNull IBetterStatsGui gui) {
		//begin context menu building
		return new TContextMenu.Builder(Objects.requireNonNull(gui.getClient()))
				//the vanilla screen button opens the vanilla stats screen
				.addButton(gui("statistics/item_picked_up").append(" ").append(BSSLang.gui_menubar_view_vanillaScreen()), __ -> {
					final var client = Objects.requireNonNull(__.getClient(), "Missing 'client' instance");
					final var player = Objects.requireNonNull(client.player, "Missing 'local player' instance");
					@SuppressWarnings("DataFlowIssue")
					final var screen = new StatsScreen(client.screen, player.getStats());
					client.setScreen(screen);
				})
				.addSeparator()
				//the stats tab submenu allows switching between stats tabs
				.addContextMenu(gui("statistics/item_used").append(" ").append(BSSLang.gui_menubar_view_statsTab()), menu_view_tab(gui))
				.build();
	}

	/**
	 * Creates a {@link TContextMenu} that allows switching between the available
	 * {@link StatsTab}s.
	 * @param gui The {@link IBetterStatsGui} instance.
	 */
	@ApiStatus.Internal
	private static final TContextMenu menu_view_tab(@NotNull IBetterStatsGui gui) {
		//create the builder
		final var builder = new TContextMenu.Builder(Objects.requireNonNull(gui.getClient()));
		//iterate all registered stats tabs and add a button for each
		for(final var mapEntry : STATS_TAB.entrySet()) {
			final var tab = mapEntry.getValue();
			builder.addButton(tab.getDisplayName(), __ -> { gui.setStatsTab(tab); gui.refresh(); });
		}
		//build and return the built context menu
		return builder.build();
	}
	// --------------------------------------------------
	/***
	 * Creates a {@link TContextMenu} for the "About" menubar button.
	 * @param gui The {@link IBetterStatsGui} instance.
	 */
	@ApiStatus.Internal
	private static final TContextMenu menu_about(@NotNull IBetterStatsGui gui) {
		return new TContextMenu.Builder(Objects.requireNonNull(gui.getClient()))
				.addButton(
						item("item/filled_map").append(" ").append(BSSLang.gui_menubar_about_sourceCode()),
						__ -> showUrlScreen(getProperty("mod.link.sources"), true))
				.addButton(
						head("MHF_Spider").append(" ").append(translatable("menu.reportBugs")),
						__ -> showUrlScreen(getProperty("mod.link.issues"), true))
				.addSeparator()
				.addButton(
						gui(BSSSprites.gui_icon_faviconCf()).append(" ").append(literal("CurseForge")),
						__ -> showUrlScreen(getProperty("mod.link.curseforge"), true))
				.addButton(
						gui(BSSSprites.gui_icon_faviconMr()).append(" ").append(literal("Modrinth")),
						__ -> showUrlScreen(getProperty("mod.link.modrinth"), true))
				.addSeparator()
				.addButton(
						gui(BSSSprites.gui_icon_heart()).append(" ").append(BSSLang.gui_menubar_about_supportMe().withStyle(ChatFormatting.YELLOW)),
						__ -> showUrlScreen(getProperty("mod.link.support_me"), true))
				.build();
	}
	// ==================================================
	/**
	 * Opens a {@link ConfirmLinkScreen} that asks the user whether
	 * they want to open the specified URI.
	 * @param uri The URI to show.
	 * @param isTrusted Whether the URI is trusted.
	 */
	@SuppressWarnings("SameParameterValue")
	private static final void showUrlScreen(@NotNull String uri, boolean isTrusted)
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
	// --------------------------------------------------
	/**
	 * Opens a {@link TFileChooserScreen} that allows the user to select an
	 * {@link McbsFile} to open and load to an {@link IBetterStatsGui}.
	 * @param gui The {@link IBetterStatsGui} instance.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@ApiStatus.Internal
	private static final void showOpenFileDialog(@NotNull IBetterStatsGui gui) throws NullPointerException
	{
		final var client     = Objects.requireNonNull(gui.getClient(), "Missing 'client' instance");
		final var lastScreen = client.screen;
		final var dialog     = new TFileChooserScreen.Builder(TFileChooserScreen.Mode.CHOOSE_FILE)
				.setLastScreen(lastScreen)
				.setFileFilter(TFileChooserScreen.FileFilter.extname("json"))
				.build((result, file) -> TUtils.uncheckedCall(() ->
				{
					//only handle file approval
					if(result != TFileChooserScreen.Result.APPROVE || file == null || !file.exists())
						return;
					//read the file's json and parse it
					final var json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
					final var mcbs = McbsIO.loadFromJson(new Gson().fromJson(json, JsonObject.class));
					//set the loaded mcbs file
					gui.setMcbsFile(mcbs);
				}));
		client.setScreen(dialog.getAsScreen());
	}

	/**
	 * Opens a {@link TFileChooserScreen} that allows the user to select a
	 * location to save the an {@link IBetterStatsGui}'s {@link McbsFile}.
	 * @param gui The {@link IBetterStatsGui} instance.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	@ApiStatus.Internal
	private static final void showSaveFileDialog(@NotNull IBetterStatsGui gui) throws NullPointerException
	{
		final var client     = Objects.requireNonNull(gui.getClient(), "Missing 'client' instance");
		final var lastScreen = client.screen;
		final var dialog     = new TFileChooserScreen.Builder(TFileChooserScreen.Mode.CREATE_FILE)
				.setLastScreen(lastScreen)
				.setFileFilter(TFileChooserScreen.FileFilter.extname("json"))
				.build((result, file) -> TUtils.uncheckedCall(() ->
				{
					//only handle file approval
					if(result != TFileChooserScreen.Result.APPROVE || file == null)
						return;
					//obtain the mcbs file and convert it to json
					final var mcbs = gui.getMcbsFile();
					final var json = McbsIO.saveToJson(mcbs);
					//write the json to the specified file
					FileUtils.writeStringToFile(file, json.toString(), StandardCharsets.UTF_8);
				}));
		client.setScreen(dialog.getAsScreen());
	}
	// ==================================================
}
