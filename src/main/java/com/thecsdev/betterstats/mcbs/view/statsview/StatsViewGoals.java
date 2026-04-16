package com.thecsdev.betterstats.mcbs.view.statsview;

import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoal;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils;
import com.thecsdev.betterstats.resource.BLanguage;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.misc.TFillColorElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import static com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils.GAP;
import static com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils.initSearchFilter;
import static com.thecsdev.commonmc.resource.TComponent.gui;

/**
 * {@link StatsView} that displays {@link McbsGoal}s.
 */
@Environment(EnvType.CLIENT)
public final @ApiStatus.Internal class StatsViewGoals extends StatsView
{
	// ==================================================
	/**
	 * The main instance of this {@link Class}.
	 */
	public static final StatsViewGoals INSTANCE = new StatsViewGoals();
	// ==================================================
	private StatsViewGoals() {}
	// ==================================================
	public final @Override @NotNull Component getDisplayName() {
		return gui("container/cartography_table/map").append(" ").append(BLanguage.gui_statsview_stats_mcbsGoals());
	}
	// --------------------------------------------------
	public final @Override void initFilters(@NotNull FiltersInitContext context) {
		StatsViewUtils.initDefaultFilters(context);
		initSearchFilter(context);
	}
	// --------------------------------------------------
	public final @Override void initStats(@NotNull StatsInitContext context) {
		final var panel = context.getPanel();
		initSummary(panel);
		initGoals(panel);
	}
	// ==================================================
	/**
	 * Initializes the 'summary about the goals feature' label onto a given
	 * {@link TPanelElement}.
	 * @param panel The target {@link TPanelElement}.
	 */
	@ApiStatus.Internal
	private static final void initSummary(@NotNull TPanelElement panel)
	{
		//background fill-color
		final var pnl_summary = new TFillColorElement(0xFF303030, 0xFF000000);
		pnl_summary.setBounds(panel.computeNextYBounds(1024, GAP));

		//the label
		final var lbl_summary = new TLabelElement(
				Component.literal("")
				.append(BLanguage.gui_statsview_stats_mcbsGoals_summaryPrefix().withStyle(ChatFormatting.YELLOW))
				.append(" ")
				.append(BLanguage.gui_statsview_stats_mcbsGoals_summary()));
		lbl_summary.wrapTextProperty().set(true, StatsViewGoals.class);
		lbl_summary.textScaleProperty().set(0.9, StatsViewGoals.class);
		lbl_summary.textColorProperty().set(0xEEFFFFFF, StatsViewGoals.class);
		lbl_summary.setBounds(pnl_summary.getBounds().add(7, 7, -14, -14));
		lbl_summary.setBoundsToFitText(lbl_summary.getBounds().width);

		//finalize and add elements
		pnl_summary.setBounds(pnl_summary.getBounds().height(lbl_summary.getBounds().height + 14));
		pnl_summary.add(lbl_summary);
		panel.add(pnl_summary);
	}
	// --------------------------------------------------
	/**
	 * Initializes the GUI for the list of {@link McbsGoal}s, onto a given
	 * {@link TPanelElement}.
	 * @param panel The target {@link TPanelElement}.
	 */
	@ApiStatus.Internal
	private static final void initGoals(@NotNull TPanelElement panel)
	{
		//FIXME - IMPLEMENT
	}
	// ==================================================
}
