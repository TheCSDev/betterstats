package com.thecsdev.betterstats.resources;

import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.commonmc.api.client.gui.misc.TTextureElement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;

import static com.thecsdev.betterstats.BetterStats.MOD_ID;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

/**
 * {@link BetterStats}'s {@link Identifier}s for {@link TextureAtlasSprite}s.
 * @see AtlasIds
 * @see TextureAtlasSprite
 * @see TTextureElement.Mode#GUI_SPRITE
 */
public final class BSSSprites
{
	// ==================================================
	private BSSSprites() {}
	// ==================================================
	public static final Identifier gui_editor_menubar_background() { return fromNamespaceAndPath(MOD_ID, "editor/menubar/background"); }
	public static final Identifier gui_editor_menubar_foreground() { return fromNamespaceAndPath(MOD_ID, "editor/menubar/foreground"); }

	public static final Identifier gui_editor_tabStrip_background() { return fromNamespaceAndPath(MOD_ID, "editor/tab_strip/background"); }
	public static final Identifier gui_editor_tabStrip_foreground() { return fromNamespaceAndPath(MOD_ID, "editor/tab_strip/foreground"); }
	public static final Identifier gui_editor_tabStrip_entry() { return fromNamespaceAndPath(MOD_ID, "editor/tab_strip/entry"); }
	public static final Identifier gui_editor_tabStrip_entrySelected() { return fromNamespaceAndPath(MOD_ID, "editor/tab_strip/entry_selected"); }

	public static final Identifier gui_editor_tab_background() { return fromNamespaceAndPath(MOD_ID, "editor/tab/background"); }
	public static final Identifier gui_editor_tab_foreground() { return fromNamespaceAndPath(MOD_ID, "editor/tab/foreground"); }

	public static final Identifier gui_editor_tab_statsFile_filtersBackground() { return fromNamespaceAndPath(MOD_ID, "editor/tab/stats_file/filters_background"); }
	public static final Identifier gui_editor_tab_statsFile_filtersForeground() { return fromNamespaceAndPath(MOD_ID, "editor/tab/stats_file/filters_foreground"); }
	public static final Identifier gui_editor_tab_statsFile_statsBackground() { return fromNamespaceAndPath(MOD_ID, "editor/tab/stats_file/stats_background"); }
	public static final Identifier gui_editor_tab_statsFile_statsForeground() { return fromNamespaceAndPath(MOD_ID, "editor/tab/stats_file/stats_foreground"); }
	// --------------------------------------------------
	public static final Identifier gui_icon_close() { return fromNamespaceAndPath(MOD_ID, "icon/close"); }
	public static final Identifier gui_icon_settings() { return fromNamespaceAndPath(MOD_ID, "icon/settings"); }
	public static final Identifier gui_icon_heart() { return fromNamespaceAndPath(MOD_ID, "icon/heart"); }
	public static final Identifier gui_icon_heartBss() { return fromNamespaceAndPath(MOD_ID, "icon/heart_bss"); }
	// --------------------------------------------------
	public static final Identifier gui_icon_filterSort() { return fromNamespaceAndPath(MOD_ID, "icon/filter_sort"); }
	public static final Identifier gui_icon_filterGroup() { return fromNamespaceAndPath(MOD_ID, "icon/filter_group"); }
	public static final Identifier gui_icon_filterUnitDist() { return fromNamespaceAndPath(MOD_ID, "icon/filter_unit_dist"); }
	public static final Identifier gui_icon_filterUnitTime() { return fromNamespaceAndPath(MOD_ID, "icon/filter_unit_time"); }
	// --------------------------------------------------
	public static final Identifier gui_icon_faviconCf() { return fromNamespaceAndPath(MOD_ID, "icon/favicon_curseforge"); }
	public static final Identifier gui_icon_faviconMr() { return fromNamespaceAndPath(MOD_ID, "icon/favicon_modrinth"); }
	public static final Identifier gui_icon_faviconWiki() { return fromNamespaceAndPath(MOD_ID, "icon/favicon_mcwiki"); }
	// ==================================================
}
