package com.thecsdev.betterstats.resource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.thecsdev.betterstats.BetterStats;

/**
 * {@link BetterStats}'s utility {@link Codec} implementations used for serialization
 * and deserialization of various objects.
 */
public final class BCodecs
{
	// ==================================================
	/**
	 * {@link Codec} for {@link java.net.URI}s.
	 */
	public static final Codec<java.net.URI> URI = Codec.STRING.flatXmap(
			uri -> {
				try { return DataResult.success(java.net.URI.create(uri)); }
				catch(Exception e) { return DataResult.error(() -> e.getClass() + ": " + e.getMessage()); }
			},
			uri -> {
				try { return DataResult.success(uri.toString()); }
				catch(Exception e) { return DataResult.error(() -> e.getClass() + ": " + e.getMessage()); }
			}
	);
	// ==================================================
}
