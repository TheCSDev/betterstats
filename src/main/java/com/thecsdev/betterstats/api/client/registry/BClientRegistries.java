package com.thecsdev.betterstats.api.client.registry;

import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.api.mcbs.view.menubar.MenubarItem;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.betterstats.mcbs.view.menubar.MenubarItemAbout;
import com.thecsdev.betterstats.mcbs.view.menubar.MenubarItemFile;
import com.thecsdev.betterstats.mcbs.view.menubar.MenubarItemView;
import com.thecsdev.betterstats.mcbs.view.statsview.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import static com.mojang.serialization.Lifecycle.stable;
import static com.thecsdev.betterstats.BetterStats.MOD_ID;
import static com.thecsdev.betterstats.api.registry.BRegistries.id;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;
import static net.minecraft.resources.ResourceKey.createRegistryKey;

/**
 * {@link BetterStats}'s client-sided registries for adding features to the mod.
 * <p>
 * <b>Important note:</b><br>
 * These {@link Registry}s are <b>NOT</b> registered in the game's <b>ROOT</b>
 * {@link BuiltInRegistries#REGISTRY}! Avoid any and all operations that involve
 * the game's <b>ROOT</b> registry!
 */
@Environment(EnvType.CLIENT)
public final class BClientRegistries
{
	// ==================================================
	private BClientRegistries() {}
	// ==================================================
	/**
	 * {@link Registry} for {@link MenubarItem}s.<br>
	 * Registered entries appear on the statistics screen's top-side menu-bar.
	 */
	public static final Registry<MenubarItem> MENUBAR_ITEM;

	/**
	 * {@link Registry} for {@link StatsView}s.<br>
	 * Registered entries appear the "stats view" dropdown.
	 */
	public static final Registry<StatsView> STATS_VIEW;
	// ==================================================
	public static final void bootstrap() { /*invokes <clinit>*/ }
	static
	{
		//create registry instances
		MENUBAR_ITEM = new MappedRegistry<>(createRegistryKey(id("menubar_item")), stable());
		STATS_VIEW   = new MappedRegistry<>(createRegistryKey(id("stats_view")), stable());

		//register menubar items
		Registry.register(MENUBAR_ITEM, id("file"),  MenubarItemFile.INSTANCE);
		Registry.register(MENUBAR_ITEM, id("view"),  MenubarItemView.INSTANCE);
		Registry.register(MENUBAR_ITEM, id("about"), MenubarItemAbout.INSTANCE);

		//register stats views
		Registry.register(STATS_VIEW, id("general"), StatsViewGeneral.INSTANCE);
		Registry.register(STATS_VIEW, id("items"),   StatsViewItems.INSTANCE);
		Registry.register(STATS_VIEW, id("blocks"),  StatsViewBlocks.INSTANCE);
		Registry.register(STATS_VIEW, id("mobs"),    StatsViewMobs.INSTANCE);
		Registry.register(STATS_VIEW, id("food"),    StatsViewFood.INSTANCE);
		Registry.register(STATS_VIEW, id("hunter"),  StatsViewHunter.INSTANCE);
	}
	// ==================================================
}
