package com.thecsdev.betterstats.resources;

import com.thecsdev.betterstats.BetterStats;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.network.chat.Component.translatable;

/**
 * {@link BetterStats}'s language translation keys.
 */
public final class BSSLang
{
	// ==================================================
	private BSSLang() {}
	// ==================================================
	public static final MutableComponent betterstats() { return translatable("betterstats"); }
	// ==================================================
	public static final MutableComponent config_common_registerCommands() { return translatable("betterstats.config.common.register_commands"); }
	public static final MutableComponent config_common_registerCommands_tooltip() { return translatable("betterstats.config.common.register_commands.tooltip"); }
	public static final MutableComponent config_client_guiMobsFollowCursor() { return translatable("betterstats.config.client.gui_mobs_follow_cursor"); }
	public static final MutableComponent config_client_guiMobsFollowCursor_tooltip() { return translatable("betterstats.config.client.gui_mobs_follow_cursor.tooltip"); }
	// --------------------------------------------------
	public static final MutableComponent cmd_stats_edit_out(@NotNull Component stat, int affectedPlayerCount) { return translatable("commands.statistics.edit.output", stat, affectedPlayerCount); }
	public static final MutableComponent cmd_stats_clear_out(int affectedPlayerCount) { return translatable("commands.statistics.clear.output", affectedPlayerCount); }
	public static final MutableComponent cmd_stats_clear_kick() { return translatable("commands.statistics.clear.kick"); }
	public static final MutableComponent cmd_stats_query_out(@NotNull Component player, @NotNull Component stat, int value) { return translatable("commands.statistics.query.output", player, stat, value); }
	// --------------------------------------------------
	public static final MutableComponent gui_menubar_file() { return translatable("betterstats.gui.menubar.file"); }
	public static final MutableComponent gui_menubar_file_new() { return translatable("betterstats.gui.menubar.file.new"); }
	public static final MutableComponent gui_menubar_file_open() { return translatable("betterstats.gui.menubar.file.open"); }
	public static final MutableComponent gui_menubar_file_saveAs() { return translatable("betterstats.gui.menubar.file.save_as"); }
	public static final MutableComponent gui_menubar_file_settings() { return translatable("betterstats.gui.menubar.file.settings"); }
	public static final MutableComponent gui_menubar_file_close() { return translatable("betterstats.gui.menubar.file.close"); }
	public static final MutableComponent gui_menubar_view() { return translatable("betterstats.gui.menubar.view"); }
	public static final MutableComponent gui_menubar_view_vanillaScreen() { return translatable("betterstats.gui.menubar.view.vanilla_screen"); }
	public static final MutableComponent gui_menubar_view_localPlayerStats() { return translatable("betterstats.gui.menubar.view.local_player_stats"); }
	public static final MutableComponent gui_menubar_view_statsView() { return translatable("betterstats.gui.menubar.view.stats_view"); }
	public static final MutableComponent gui_menubar_about() { return translatable("betterstats.gui.menubar.about"); }
	public static final MutableComponent gui_menubar_about_sourceCode() { return translatable("betterstats.gui.menubar.about.source_code"); }
	public static final MutableComponent gui_menubar_about_supportMe() { return translatable("betterstats.gui.menubar.about.support_me"); }
	// --------------------------------------------------
	public static final MutableComponent gui_statsview_filters() { return translatable("betterstats.gui.statsview.filters"); }
	public static final MutableComponent gui_statsview_filter_selectedView() { return translatable("betterstats.gui.statsview.filter.selected_view"); }
	public static final MutableComponent gui_statsview_filter_search() { return translatable("betterstats.gui.statsview.filter.search"); }
	public static final MutableComponent gui_statsview_filter_showAllStats() { return translatable("betterstats.gui.statsview.filter.show_all_stats"); }
	public static final MutableComponent gui_statsview_filter_sortBy() { return translatable("betterstats.gui.statsview.filter.sort_by"); }
	public static final MutableComponent gui_statsview_filter_groupBy() { return translatable("betterstats.gui.statsview.filter.group_by"); }
	public static final MutableComponent gui_statsview_filter_groupBy_all() { return translatable("betterstats.gui.statsview.filter.group_by.all"); }
	public static final MutableComponent gui_statsview_filter_groupBy_mod() { return translatable("betterstats.gui.statsview.filter.group_by.mod"); }
	public static final MutableComponent gui_statsview_filter_groupBy_mobCategory() { return translatable("betterstats.gui.statsview.filter.group_by.mob_category"); }
	public static final MutableComponent gui_statsview_filter_groupBy_createiveModeTab() { return translatable("betterstats.gui.statsview.filter.group_by.creative_mode_tab"); }
	public static final MutableComponent gui_statsview_filter_distanceUnit() { return translatable("betterstats.gui.statsview.filter.distance_unit"); }
	public static final MutableComponent gui_statsview_filter_timeUnit() { return translatable("betterstats.gui.statsview.filter.time_unit"); }
	public static final MutableComponent gui_statsview_stats_noStats() { return translatable("betterstats.gui.statsview.stats.no_stats"); }
	public static final MutableComponent gui_statsview_stats_ctxMenu_viewOnWiki() { return translatable("betterstats.gui.statsview.stats.ctxmenu.view_on_wiki"); }
	// ==================================================
}
