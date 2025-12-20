package com.thecsdev.betterstats.client.gui.mcbs_view.statsview;

import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.commonmc.api.stats.util.ItemStats;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import static com.thecsdev.commonmc.resources.TComponent.item;
import static net.minecraft.network.chat.Component.translatable;

/**
 * {@link StatsView} that displays "Item" statistics.
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public sealed class StatsViewItems extends SubjectStatsView<ItemStats> permits StatsViewFood
{
	// ==================================================
	/**
	 * The main instance of this {@link Class}.
	 */
	public static final StatsViewItems INSTANCE = new StatsViewItems();
	// ==================================================
	protected StatsViewItems() {}
	// ==================================================
	public @Override @NotNull Component getDisplayName() {
		return item("item/compass_12").append(" ").append(translatable("stat.itemsButton"));
	}
	// ==================================================
	public @Override void initStats(@NotNull StatsInitContext context) {}
	// ==================================================
}
