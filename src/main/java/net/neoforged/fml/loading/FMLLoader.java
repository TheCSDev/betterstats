package net.neoforged.fml.loading;

public final class FMLLoader
{
	private FMLLoader() {}
	public static FMLLoader getCurrent() { throw new AssertionError(); }
	public LoadingModList getLoadingModList() { throw new AssertionError(); }
}
