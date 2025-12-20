package com.thecsdev.betterstats.client.gui.mcbs_view.statsview;

import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import static com.thecsdev.commonmc.resources.TComponent.item;
import static net.minecraft.network.chat.Component.translatable;

/**
 * {@link StatsView} that displays "Food & Drinks" statistics.
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class StatsViewFood extends StatsViewItems
{
	// ==================================================
	/**
	 * The main instance of this {@link Class}.
	 */
	public static final StatsViewFood INSTANCE = new StatsViewFood();
	// ==================================================
	private StatsViewFood() {}
	// ==================================================
	public @Override @NotNull Component getDisplayName() {
		return item("item/apple").append(" ").append(translatable("itemGroup.foodAndDrink"));
	}
	// ==================================================
	public @Override void initStats(@NotNull StatsInitContext context) {}
	// ==================================================
}
