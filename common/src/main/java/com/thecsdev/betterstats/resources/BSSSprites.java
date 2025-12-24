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
	public static final Identifier gui_panel_bgFilters() { return fromNamespaceAndPath(MOD_ID, "panel/bg_filters"); }
	public static final Identifier gui_panel_fgFilters() { return fromNamespaceAndPath(MOD_ID, "panel/fg_filters"); }
	public static final Identifier gui_panel_bgStats() { return fromNamespaceAndPath(MOD_ID, "panel/bg_stats"); }
	public static final Identifier gui_panel_fgStats() { return fromNamespaceAndPath(MOD_ID, "panel/fg_stats"); }
	public static final Identifier gui_panel_bgMenubar() { return fromNamespaceAndPath(MOD_ID, "panel/bg_menubar"); }
	public static final Identifier gui_panel_fgMenubar() { return fromNamespaceAndPath(MOD_ID, "panel/fg_menubar"); }
	public static final Identifier gui_panel_bgTabstrip() { return fromNamespaceAndPath(MOD_ID, "panel/bg_tabstrip"); }
	public static final Identifier gui_panel_fgTabstrip() { return fromNamespaceAndPath(MOD_ID, "panel/fg_tabstrip"); }
	public static final Identifier gui_panel_tabentry() { return fromNamespaceAndPath(MOD_ID, "panel/tabentry"); }
	public static final Identifier gui_panel_tabentrySelected() { return fromNamespaceAndPath(MOD_ID, "panel/tabentry_selected"); }
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
