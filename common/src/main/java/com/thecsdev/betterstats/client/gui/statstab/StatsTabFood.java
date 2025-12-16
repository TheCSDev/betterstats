package com.thecsdev.betterstats.client.gui.statstab;

import com.thecsdev.common.util.annotations.Virtual;
import com.thecsdev.commonmc.api.client.gui.widget.stats.TItemStatsWidget;
import com.thecsdev.commonmc.api.stats.util.ItemStats;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

import static com.thecsdev.betterstats.api.client.gui.statstab.StatsTabUtils.FID_SEARCH;
import static com.thecsdev.commonmc.resources.TComponent.item;
import static net.minecraft.network.chat.Component.translatable;

/**
 * A stats tab that displays food and drink related {@link Item} stats.
 */
public final class StatsTabFood extends StatsTabItems
{
	// ==================================================
	public static final StatsTabFood INSTANCE = new StatsTabFood();
	// ==================================================
	public final @Override @NotNull Component getDisplayName() {
		return item("item/apple").append(" ").append(translatable("itemGroup.foodAndDrink"));
	}
	// --------------------------------------------------
	protected @Virtual @Override @NotNull Predicate<ItemStats> getStatsPredicate(final @NotNull Filters filters) throws NullPointerException {
		//get filter values
		final var f_query = filters.getProperty(String.class, FID_SEARCH, "");
		//construct and return predicate
		return stat -> stat.getSubject().components().has(DataComponents.FOOD) && stat.isSearchMatch(f_query);
	}
	// --------------------------------------------------
	protected final @Override void postProcessWidget(@NotNull StatsInitContext context, @NotNull TItemStatsWidget widget)
	{
		//super post process
		super.postProcessWidget(context, widget);
		//obtain stats and assert non-null
		final var stats = widget.statsProperty().get();
		assert stats != null;
		//set outline color based on usage
		if(stats.getTimesUsed() > 0)
			widget.outlineColorProperty().set(OC_DONE, StatsTabFood.class);
		else if(!stats.isEmpty())
			widget.outlineColorProperty().set(OC_INPROGRESS, StatsTabFood.class);
	}
	// ==================================================
}
