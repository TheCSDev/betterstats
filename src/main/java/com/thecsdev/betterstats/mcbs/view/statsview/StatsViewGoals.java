package com.thecsdev.betterstats.mcbs.view.statsview;

import com.thecsdev.betterstats.api.mcbs.model.McbsFile;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoal;
import com.thecsdev.betterstats.api.mcbs.view.goal.McbsGoalGUI;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils;
import com.thecsdev.betterstats.client.gui.widget.BGoalProgressBar;
import com.thecsdev.betterstats.resource.BLanguage;
import com.thecsdev.betterstats.resource.BSprites;
import com.thecsdev.common.math.Bounds2i;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.misc.TTextureElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.render.TGuiGraphics;
import com.thecsdev.commonmc.api.client.gui.widget.TButtonWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils.initSearchFilter;
import static com.thecsdev.commonmc.resource.TComponent.gui;

/**
 * {@link StatsView} that displays {@link McbsGoal}s.
 */
@Environment(EnvType.CLIENT)
public final @ApiStatus.Internal class StatsViewGoals extends StatsView
{
	// ================================================== ==================================================
	//                                     StatsViewGoals IMPLEMENTATION
	// ================================================== ==================================================
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
		initGoals(context);
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
		final var gLabel = StatsViewUtils.initGroupLabelFramed(
				panel,
				Component.literal("")
					.append(BLanguage.gui_statsview_stats_mcbsGoals_summaryPrefix().withStyle(ChatFormatting.YELLOW))
					.append(" ")
					.append(BLanguage.gui_statsview_stats_mcbsGoals_summary()),
				0.9d);
		gLabel.getValue().textColorProperty().set(0xEEFFFFFF, StatsViewGoals.class);
	}
	// --------------------------------------------------
	/**
	 * Initializes the "goals" GUI for a given {@link StatsInitContext}.
	 * @param context The {@link StatsInitContext}.
	 */
	@ApiStatus.Internal
	private static final void initGoals(@NotNull StatsInitContext context)
	{
		final var panel = context.getPanel();
		context.getTab().getGoals().forEach((_, goal) -> {
			final var item = new ListedGoalGui<>(context, goal);
			item.setBounds(panel.computeNextYBounds(45, 6));
			panel.add(item);
		});
	}
	// ================================================== ==================================================
	//                                      ListedGoalGui IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * GUI for an {@link McbsGoal} listen in the 'goals' list.
	 */
	@ApiStatus.Internal
	private static final class ListedGoalGui<T extends McbsGoal> extends TElement
	{
		// ==================================================
		//background sprite identifier
		private static final Identifier ID_BG = BSprites.gui_editor_goal_listedGoalBg();
		// --------------------------------------------------
		private final @NotNull  T                 goal;
		private final @Nullable McbsGoalGUI<T>    goalGui;
		private final @NotNull  McbsFile          file;
		// --------------------------------------------------
		private final @NotNull TTextureElement  el_icon;
		private final @NotNull TLabelElement    el_lblTitle;
		private final @NotNull BGoalProgressBar el_progrBar;
		private final @NotNull TLabelElement    el_lblProgr;
		private final @NotNull TButtonWidget    el_btnEdit;
		private final @NotNull TButtonWidget    el_btnDelete;
		// ==================================================
		public ListedGoalGui(@NotNull StatsInitContext context, @NotNull T goal)
		{
			//initialize fields
			Objects.requireNonNull(context);
			this.goal    = Objects.requireNonNull(goal);
			this.goalGui = McbsGoalGUI.findFor(goal);
			this.file    = context.getTab().getMcbsFile();

			//initialize gui elements
			this.el_icon      = new TTextureElement(BSprites.gui_editor_goal_listedGoalIconBg());
			this.el_lblTitle  = new TLabelElement(this.goal.getType().getName());
			this.el_lblTitle.textScaleProperty().set(1.1d, ListedGoalGui.class);
			this.el_lblTitle.dropShadowProperty().set(false, ListedGoalGui.class);
			this.el_progrBar  = new BGoalProgressBar(this.goal.getProgress(this.file));
			this.el_lblProgr  = new TLabelElement(this.goal.getProgressText(this.file));
			this.el_lblProgr.textColorProperty().set(0xFFf3e7b7, ListedGoalGui.class);
			this.el_btnEdit   = new TButtonWidget();
			this.el_btnDelete = new TButtonWidget();
		}
		// ==================================================
		public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.drawGuiSprite(ID_BG, bb.x, bb.y, bb.width, bb.height, 0xFFFFFFFF);
		}
		// --------------------------------------------------
		protected final @Override void initCallback()
		{
			// ---------- preparation
			final var bb     = getBounds();
			final var pad    = 5;
			final var bb_ico = new Bounds2i(
					bb.x + pad,             //x
					bb.y + pad,             //y
					bb.height - (pad * 2),  //width
					bb.height - (pad * 2)); //height
			final var bb_right = new Bounds2i(
					bb_ico.endX + pad + 3,                     //x
					bb.y        + pad + 3,                     //y
					bb.width  - bb_ico.width - (pad * 2) - 12, //width
					bb.height                - (pad * 2) - 6); //height

			// ---------- left side
			//place the goal icon element
			this.el_icon.setBounds(bb_ico);
			if(this.goalGui != null) { //reinitialize icon gui
				this.el_icon.clear();  //clearing old gui is necessary ofc.
				this.goalGui.initIcon(this.goal, this.el_icon, bb_ico.height / 8);
			}
			add(this.el_icon);

			// ---------- right side
			//the right-side gui panel
			final var el_right = new TElement();
			el_right.setBounds(bb_right);
			el_right.hoverableProperty().set(false, ListedGoalGui.class);
			el_right.focusableProperty().set(false, ListedGoalGui.class);
			add(el_right);

			final var el_rightBtns = new TElement();
			el_rightBtns.setBounds(bb_right);
			el_rightBtns.hoverableProperty().set(false, ListedGoalGui.class);
			el_rightBtns.focusableProperty().set(false, ListedGoalGui.class);
			add(el_rightBtns);

			//goal title label
			this.el_lblTitle.setBounds(0, 0, bb_right.width, 12);
			this.el_lblTitle.setText(this.goal.getType().getName());
			el_right.addRel(this.el_lblTitle);

			//goal progress bar
			this.el_progrBar.setBounds(0, 12 + 4, (int) (bb_right.width / 2.7d), 12);
			this.el_progrBar.setValue(this.goal.getProgress(this.file));
			el_right.addRel(this.el_progrBar);

			//goal progress label
			this.el_lblProgr.setBounds(
					this.el_progrBar.getBounds().endX + 7,
					this.el_progrBar.getBounds().y,
					bb_right.width / 3,
					this.el_progrBar.getBounds().height);
			this.el_lblProgr.setText(this.goal.getProgressText(this.file));
			el_right.add(this.el_lblProgr);

			// ---------- far-right-side buttons
			placeRightSideButton(el_rightBtns, this.el_btnEdit, BSprites.gui_icon_pencil());
			placeRightSideButton(el_rightBtns, this.el_btnDelete, BSprites.gui_icon_trash());
		}

		/**
		 * Places a {@link TButtonWidget} to the right side of a given {@link TElement}.
		 * If said {@link TElement} already has buttons, the next button is placed to
		 * the left side of the last button.
		 * @param onto The target {@link TElement} to place onto.
		 * @param button The {@link TButtonWidget} to place.
		 * @param icon A {@link TTextureElement} is created over the button, with this icon.
		 * @throws NullPointerException If an argument is {@code null}.
		 */
		@SuppressWarnings("SuspiciousNameCombination")
		private static final @ApiStatus.Internal void placeRightSideButton(
				@NotNull TElement onto, @NotNull TButtonWidget button, @NotNull Identifier icon)
				throws NullPointerException
		{
			//place the button
			final var bb     = onto.getBounds();
			final int size   = onto.size() / 2; //icons are also elements, don't account for them
			final var bb_btn = new Bounds2i(
					bb.endX - (bb.height * (size + 1)) - (3 * size),
					bb.y,
					bb.height,
					bb.height);
			button.setBounds(bb_btn);
			onto.add(button);

			//set the button's visibility to 'false' if 'onto' is not big enough (width)
			//(the larger the 'size', the larger width demand there is)
			button.visibleProperty().set(
					(bb.width / 3) > (bb.height * (size + 1)) + (3 * size),
					ListedGoalGui.class);

			//create and place the icon over the button
			final var el_icon = new TTextureElement(icon);
			el_icon.setBounds(bb_btn.x + 5, bb_btn.y + 5, bb_btn.width - 10, bb_btn.height - 10);
			el_icon.visibleProperty().set(button.isVisible(), ListedGoalGui.class);
			onto.add(el_icon);
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
