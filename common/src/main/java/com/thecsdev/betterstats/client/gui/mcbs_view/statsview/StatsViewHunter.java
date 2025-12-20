package com.thecsdev.betterstats.client.gui.mcbs_view.statsview;

import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import static com.thecsdev.commonmc.resources.TComponent.head;
import static com.thecsdev.commonmc.resources.TComponent.item;
import static net.minecraft.network.chat.Component.translatable;

/**
 * {@link StatsView} that displays "Hostile Mobs" statistics.
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class StatsViewHunter extends StatsViewMobs
{
	// ==================================================
	/**
	 * The main instance of this {@link Class}.
	 */
	public static final StatsViewHunter INSTANCE = new StatsViewHunter();
	// ==================================================
	private StatsViewHunter() {}
	// ==================================================
	public @Override @NotNull Component getDisplayName() {
		return head("MHF_Zombie").append(" ").append(translatable("soundCategory.hostile"));
	}
	// ==================================================
	public @Override void initStats(@NotNull StatsInitContext context) {}
	// ==================================================
}
