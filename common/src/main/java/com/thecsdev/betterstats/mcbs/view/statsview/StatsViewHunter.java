package com.thecsdev.betterstats.mcbs.view.statsview;

import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.common.util.annotations.Virtual;
import com.thecsdev.commonmc.api.client.gui.widget.stats.TEntityStatsWidget;
import com.thecsdev.commonmc.api.stats.util.EntityStats;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.MobCategory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

import static com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils.FID_SEARCH;
import static com.thecsdev.commonmc.resources.TComponent.head;
import static net.minecraft.network.chat.Component.translatable;

/**
 * {@link StatsView} that displays "Hostile Mobs" statistics.
 */
@Environment(EnvType.CLIENT)
public final @ApiStatus.Internal class StatsViewHunter extends StatsViewMobs
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
	// --------------------------------------------------
	protected @Virtual @Override @NotNull Predicate<EntityStats> getStatsPredicate(final @NotNull Filters filters) throws NullPointerException {
		//get filter values
		final var f_query = filters.getProperty(String.class, FID_SEARCH, "");
		//construct and return predicate
		return stat -> stat.getSubject().getCategory() == MobCategory.MONSTER && stat.isSearchMatch(f_query);
	}
	// --------------------------------------------------
	protected final @Override void postProcessWidget(@NotNull StatsInitContext context, @NotNull TEntityStatsWidget widget)
	{
		//super post process
		super.postProcessWidget(context, widget);
		//obtain stats and assert non-null
		final var stats = widget.statsProperty().get();
		assert stats != null;
		//set outline color based on usage
		if(stats.getKills() > 0)
			widget.outlineColorProperty().set(OC_DONE, StatsViewHunter.class);
		else if(!stats.isEmpty())
			widget.outlineColorProperty().set(OC_INPROGRESS, StatsViewHunter.class);
	}
	// ==================================================
}
