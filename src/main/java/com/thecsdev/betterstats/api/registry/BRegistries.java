package com.thecsdev.betterstats.api.registry;

import com.mojang.serialization.Lifecycle;
import com.thecsdev.betterstats.BetterStats;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoal;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoalType;
import com.thecsdev.betterstats.api.mcbs.view.goal.McbsGoalGUI;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.thecsdev.betterstats.BetterStats.MOD_ID;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;
import static net.minecraft.resources.ResourceKey.createRegistryKey;

/**
 * {@link BetterStats}'s common-sided registries for adding features to the mod.
 * <p>
 * <b>Important note:</b><br>
 * These {@link Registry}s are <b>NOT</b> registered in the game's <b>ROOT</b>
 * {@link BuiltInRegistries#REGISTRY}! Avoid any and all operations that involve
 * the game's <b>ROOT</b> registry!
 * <p>
 * These {@link Registry}s are also <b>NOT</b> synchronized between the client
 * and server!
 */
public final class BRegistries
{
	// ==================================================
	private BRegistries() {}
	// ==================================================
	/**
	 * {@link Registry} for {@link McbsGoalType}s.<br>
	 * Allows this mod to be capable of recognizing and serializing {@link McbsGoal}
	 * instances.
	 * <p>
	 * The {@link McbsGoalType} {@link Identifier}s <b>MUST</b> match the
	 * {@link Identifier}s of corresponding {@link McbsGoalGUI}s.
	 *
	 * @see McbsGoalType#getKey()
	 * @see McbsGoalGUI#getKey()
	 */
	public static final Registry<McbsGoalType<?>> GOAL_TYPE;
	// ==================================================
	public static final void bootstrap() { /*invokes <clinit>*/ }
	static
	{
		//create registry instances
		GOAL_TYPE = new MappedRegistry<>(createRegistryKey(id("goal_type")), Lifecycle.stable());

		//register goal types
		Registry.register(GOAL_TYPE, id("stat_int_value"), McbsGoalType.STAT_INT_VALUE);
	}
	// ==================================================
	/**
	 * Creates an {@link Identifier} that uses {@link BetterStats#MOD_ID}
	 * as the "namespace".
	 * @param id The {@link Identifier#getPath()} value.
	 */
	public static final @ApiStatus.Internal @NotNull Identifier id(@NotNull String id) {
		return fromNamespaceAndPath(MOD_ID, Objects.requireNonNull(id));
	}
	// ==================================================
}
