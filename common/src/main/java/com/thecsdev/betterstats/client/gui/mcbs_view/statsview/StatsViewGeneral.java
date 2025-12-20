package com.thecsdev.betterstats.client.gui.mcbs_view.statsview;

import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.commonmc.api.stats.util.CustomStat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import static com.thecsdev.commonmc.resources.TComponent.item;
import static net.minecraft.network.chat.Component.translatable;

/**
 * {@link StatsView} that displays "General" statistics.
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class StatsViewGeneral extends SubjectStatsView<CustomStat>
{
	// ================================================== ==================================================
	//                                   StatsViewGeneral IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * The main instance of this {@link Class}.
	 */
	public static final StatsViewGeneral INSTANCE = new StatsViewGeneral();
	// ==================================================
	private StatsViewGeneral() {}
	// ==================================================
	public final @Override @NotNull Component getDisplayName() {
		return item("item/paper").append(" ").append(translatable("stat.generalButton"));
	}
	// ==================================================
	public final @Override void initStats(@NotNull StatsInitContext context) {}
	// ================================================== ==================================================
}
