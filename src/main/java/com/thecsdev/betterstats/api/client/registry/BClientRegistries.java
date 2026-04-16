package com.thecsdev.betterstats.api.client.registry;

import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoal;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoalType;
import com.thecsdev.betterstats.api.mcbs.view.goal.McbsGoalGUI;
import com.thecsdev.betterstats.api.mcbs.view.menubar.MenubarItem;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.betterstats.mcbs.view.goal.McbsSivGoalGUI;
import com.thecsdev.betterstats.mcbs.view.menubar.MenubarItemAbout;
import com.thecsdev.betterstats.mcbs.view.menubar.MenubarItemFile;
import com.thecsdev.betterstats.mcbs.view.menubar.MenubarItemView;
import com.thecsdev.betterstats.mcbs.view.statsview.*;
import com.thecsdev.commonmc.TCDCommonsConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import static com.mojang.serialization.Lifecycle.stable;
import static com.thecsdev.betterstats.api.registry.BRegistries.id;
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

	/**
	 * {@link Registry} for {@link McbsGoalGUI}s.<br>
	 * Registered entries are factories that construct GUIs for {@link McbsGoal}s.
	 * <p>
	 * The {@link McbsGoalGUI} {@link Identifier}s <b>MUST</b> match the
	 * {@link Identifier}s of corresponding {@link McbsGoalType}s.
	 *
	 * @see McbsGoalType#getKey()
	 * @see McbsGoalGUI#getKey()
	 */
	public static final Registry<McbsGoalGUI<?>> GOAL_GUI;
	// ==================================================
	public static final void bootstrap() { /*invokes <clinit>*/ }
	static
	{
		//create registry instances
		MENUBAR_ITEM = new MappedRegistry<>(createRegistryKey(id("menubar_item")), stable());
		STATS_VIEW   = new MappedRegistry<>(createRegistryKey(id("stats_view")), stable());
		GOAL_GUI     = new MappedRegistry<>(createRegistryKey(id("goal_gui")), stable());

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
		if(TCDCommonsConfig.FLAG_DEV_ENV)
			Registry.register(STATS_VIEW, id("goals"), StatsViewGoals.INSTANCE);

		//register mcbs goal gui-s
		Registry.register(GOAL_GUI, id("stat_int_value"), McbsSivGoalGUI.INSTANCE);
	}
	// ==================================================
}
