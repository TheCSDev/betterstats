package com.thecsdev.betterstats.resources;

import com.thecsdev.betterstats.BetterStats;
import net.minecraft.resources.Identifier;

import static com.thecsdev.betterstats.BetterStats.MOD_ID;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;

/**
 * {@link BetterStats}'s {@link Identifier}s for resource-pack textures.
 */
public final class BSSTex
{
	// ==================================================
	private BSSTex() {}
	// ==================================================
	public static final Identifier gui_images_nostatsSilhouette() { return fromNamespaceAndPath(MOD_ID, "textures/gui/images/nostats_silhouette.png"); }
	// ==================================================
}
