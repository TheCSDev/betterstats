package com.thecsdev.betterstats.api.client.gui.screen;

import com.thecsdev.commonmc.api.client.gui.screen.TScreenWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

/**
 * {@link TScreenWrapper} implementation for {@link BetterStatsConfigScreen}.
 *
 * @apiNote Since {@code v5.7.1}, this class has been created and a constructor was
 * added, solely to make this screen accessible to {@code FancyMenu}. It is not intended
 * to be used directly as a Java API. May change at any time.
 */
public final class BetterStatsConfigScreenWrapper extends TScreenWrapper<BetterStatsConfigScreen>
{
	// ==================================================
	public BetterStatsConfigScreenWrapper() { this(Minecraft.getInstance().gui.screen()); }
	public BetterStatsConfigScreenWrapper(@Nullable Screen lastScreen) { super(new BetterStatsConfigScreen(lastScreen)); }
	// --------------------------------------------------
	BetterStatsConfigScreenWrapper(@NonNull BetterStatsConfigScreen target) { super(target); }
	// ==================================================
}
