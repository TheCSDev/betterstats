package net.neoforged.fml.loading;

import net.neoforged.api.distmarker.Dist;

public final class FMLEnvironment
{
	private FMLEnvironment() {}
	public static Dist getDist() { throw new AssertionError(); }
}
