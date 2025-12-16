package com.thecsdev.betterstats.client.gui.statstab;

import com.thecsdev.common.util.annotations.Virtual;
import com.thecsdev.commonmc.api.client.gui.widget.stats.TEntityStatsWidget;
import com.thecsdev.commonmc.api.stats.util.EntityStats;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.MobCategory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

import static com.thecsdev.betterstats.api.client.gui.statstab.StatsTabUtils.FID_SEARCH;
import static com.thecsdev.commonmc.resources.TComponent.head;
import static net.minecraft.network.chat.Component.translatable;

public final class
StatsTabHunter extends StatsTabMobs
{
	// ==================================================
	public static final StatsTabHunter INSTANCE = new StatsTabHunter();
	// ==================================================
	public final @Override @NotNull Component getDisplayName() {
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
			widget.outlineColorProperty().set(OC_DONE, StatsTabHunter.class);
		else if(!stats.isEmpty())
			widget.outlineColorProperty().set(OC_INPROGRESS, StatsTabHunter.class);
	}
	// ==================================================
}
