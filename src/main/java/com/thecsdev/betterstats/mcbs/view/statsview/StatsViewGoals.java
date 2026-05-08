package com.thecsdev.betterstats.mcbs.view.statsview;

import com.google.common.collect.HashBiMap;
import com.thecsdev.betterstats.api.mcbs.model.McbsFile;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoal;
import com.thecsdev.betterstats.api.mcbs.view.goal.McbsGoalGUI;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsView;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils;
import com.thecsdev.betterstats.client.gui.widget.BGoalProgressBar;
import com.thecsdev.betterstats.resource.BLanguage;
import com.thecsdev.betterstats.resource.BSprites;
import com.thecsdev.common.math.Bounds2i;
import com.thecsdev.common.math.UDim2;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.misc.TTextureElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.render.TGuiGraphics;
import com.thecsdev.commonmc.api.client.gui.tooltip.TTooltip;
import com.thecsdev.commonmc.api.client.gui.widget.TButtonWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static com.thecsdev.betterstats.BetterStats.MOD_ID;
import static com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils.*;
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

	/**
	 * {@link StatsView.Filters} key for whether the goal management buttons
	 * should currently be visible.
	 * <p>
	 * <b>Filter value type:</b> {@code boolean}
	 */
	public static final Identifier FID_MANAGE_GOALS = Identifier.fromNamespaceAndPath(MOD_ID, "manage_goals");
	// ==================================================
	private StatsViewGoals() {}
	// ==================================================
	public final @Override @NotNull Component getDisplayName() {
		return gui("container/cartography_table/map").append(" ").append(BLanguage.gui_statsview_stats_mcbsGoals());
	}
	// --------------------------------------------------
	public final @Override void initFilters(@NotNull FiltersInitContext context)
	{
		// ---------- default filters
		StatsViewUtils.initDefaultFilters(context);
		initSearchFilter(context);

		// ---------- preparation for other controls
		final var panel   = context.getPanel();
		final var filters = context.getFilters();

		// ---------- management buttons
		SeparatorElement.init(panel);
		//"new goal" button
		final var btn_newGoal = new TButtonWidget();
		btn_newGoal.setBounds(panel.computeNextYBounds(20, GAP));
		btn_newGoal.getLabel().setText(BLanguage.gui_statsview_stats_mcbsGoals_newBtn());
		panel.add(btn_newGoal);

		//"manage goals" button
		final var btn_manageGoals = new TButtonWidget();
		btn_manageGoals.setBounds(panel.computeNextYBounds(20, GAP));
		btn_manageGoals.getLabel().setText(BLanguage.gui_statsview_stats_mcbsGoals_manageBtn());
		btn_manageGoals.eClicked.addListener(_ -> {
			final boolean val = filters.getProperty(boolean.class, FID_MANAGE_GOALS, false);
			filters.setProperty(boolean.class, FID_MANAGE_GOALS, !val);
		});
		panel.add(btn_manageGoals);

		// ---------- goal filters
		SeparatorElement.init(panel);
		//TODO - Implement
	}
	// --------------------------------------------------
	public final @Override void initStats(@NotNull StatsInitContext context) {
		final var panel = context.getPanel();
		initAlerts(panel);
		initOverview(context);
		initGoals(context);
	}
	// ==================================================
	/**
	 * Initializes the 'summary about the goals feature' label onto a given
	 * {@link TPanelElement}.
	 * @param panel The target {@link TPanelElement}.
	 */
	@ApiStatus.Internal
	private static final void initAlerts(@NotNull TPanelElement panel)
	{
		StatsViewUtils.initGroupLabelFramed(
				panel,
				Component.literal("")
					.append(BLanguage.gui_statsview_stats_mcbsGoals_alert1Prefix().withStyle(ChatFormatting.YELLOW))
					.append(" ")
					.append(BLanguage.gui_statsview_stats_mcbsGoals_alert1()),
				0.9d)
				.getValue().textColorProperty().set(0xEEFFFFFF, StatsViewGoals.class);
		StatsViewUtils.initGroupLabelFramed(
				panel,
				Component.literal("")
						.append(BLanguage.gui_statsview_stats_mcbsGoals_alert2Prefix().withStyle(ChatFormatting.AQUA))
						.append(" ")
						.append(BLanguage.gui_statsview_stats_mcbsGoals_alert2()),
				0.9d)
				.getValue().textColorProperty().set(0xEEFFFFFF, StatsViewGoals.class);
	}
	// --------------------------------------------------
	/**
	 * Initializes the 'overview' GUI that shows things like the number
	 * of completed goals and overall completion progression.
	 * @param context The {@link StatsInitContext}.
	 */
	//TODO - Uses an anti-dry practices. Code something better.
	@ApiStatus.Internal
	private static final void initOverview(@NotNull StatsInitContext context)
	{
		// ---------- preparation
		final var panel = context.getPanel();
		final var goals = context.getTab().getGoals();
		if(goals.isEmpty()) return;
		final var file  = context.getTab().getMcbsFile(); //do not write, read only

		int done = 0, goalCount = goals.size();
		double progressSum = 0;
		for(final var goal : goals.values()) {
			final var gp = goal.getProgress(file);
			progressSum += gp;
			if(gp >= 1) done++;
		}

		// ---------- initialize group label
		StatsViewUtils.initGroupLabel(panel, BLanguage.gui_statsview_stats_mcbsGoals_overview());

		// ---------- initialize gui
		//the base 'panel' for the two entries
		final var base = new TElement();
		base.setBounds(panel.computeNextYBounds(40, 4));
		panel.add(base);

		//number of complete goals
		final var el_complete = new TTextureElement(BSprites.gui_editor_goal_listedGoalBg());
		base.add(el_complete);
		el_complete.setBounds(UDim2.ZERO, new UDim2(0.5, -2, 1, 0));

		final var ico_complete = new TTextureElement(BSprites.gui_editor_goal_listedGoalIconBg());
		ico_complete.setBounds(5, 5, 30, 30);
		el_complete.addRel(ico_complete);

		final var ico2_complete = new TTextureElement(Identifier.withDefaultNamespace("textures/item/writable_book.png"));
		ico2_complete.setBounds(5, 5, 20, 20);
		ico_complete.addRel(ico2_complete);

		final var txt_complete = new TLabelElement(Component.literal("")
				.append(BLanguage.gui_statsview_stats_mcbsGoals_overview_completedGoals())
				.append(Component.literal("\n" + done + " / " + goalCount).withColor(0xFFf3e7b7))
		);
		el_complete.add(txt_complete);
		txt_complete.setBounds(new UDim2(0, 42, 0, 0), new UDim2(1, -47, 1, 0));

		//completion percentage
		final var el_progress = new TTextureElement(BSprites.gui_editor_goal_listedGoalBg());
		base.add(el_progress);
		el_progress.setBounds(new UDim2(0.5, 2, 0, 0), new UDim2(0.5, -3, 1, 0));

		final var ico_progress = new TTextureElement(BSprites.gui_editor_goal_listedGoalIconBg());
		ico_progress.setBounds(5, 5, 30, 30);
		el_progress.addRel(ico_progress);

		final var ico2_progress = new TTextureElement(BSprites.gui_icon_filterGroup());
		ico2_progress.setBounds(5, 5, 20, 20);
		ico_progress.addRel(ico2_progress);

		final var txt_progress = new TLabelElement(Component.literal("")
				.append(BLanguage.gui_statsview_stats_mcbsGoals_overview_totalProgress())
				.append(Component.literal("\n" + new DecimalFormat("#%").format(progressSum / goalCount)).withColor(0xFFf3e7b7))
		);
		el_progress.add(txt_progress);
		txt_progress.setBounds(new UDim2(0, 42, 0, 0), new UDim2(1, -47, 1, 0));
	}
	// --------------------------------------------------
	/**
	 * Initializes the "goals" GUI for a given {@link StatsInitContext}.
	 * @param context The {@link StatsInitContext}.
	 */
	@ApiStatus.Internal
	private static final void initGoals(@NotNull StatsInitContext context)
	{
		//preparation
		final var panel         = context.getPanel();
		final var file          = context.getTab().getMcbsFile();
		final var filters       = context.getFilters();
		final var filter_search = filters.getProperty(String.class, FID_SEARCH, "");

		final var goalMap = HashBiMap.create(context.getTab().getGoals()); //shallow copy
		final var goals   = goalMap.values().stream()
				//filter based on search query
				.filter(goal -> {
					if(filter_search.isBlank()) return true;
					final var a = (goalMap.inverse().get(goal).toString() + goal.getType().getName().getString() + goal.getObjectiveText().getString())
							.toLowerCase(Locale.ROOT).replaceAll("\\s+", "");
					final var b = filter_search.toLowerCase(Locale.ROOT).replaceAll("\\s+", "");
					return a.contains(b);
				})
				//then sort based on progress
				.sorted(Comparator.comparing((McbsGoal goal) -> goal.getProgress(file)).reversed())
				//lastly, convert to list
				.toList();

		//initialize gui
		StatsViewUtils.initGroupLabel(panel, BLanguage.gui_statsview_stats_mcbsGoals());
		if(!goals.isEmpty()) {
			//initialize gui for each goal entry
			for(final var goal : goals) {
				final var el_goal = new ListedGoalGui<>(context, goal);
				el_goal.setBounds(panel.computeNextYBounds(40, 4));
				panel.add(el_goal);
			}
		} else {
			//initialize "no goals" label
			final var lbl_noGoals = new TLabelElement(BLanguage.gui_statsview_stats_noGoals());
			lbl_noGoals.setBounds(panel.computeNextYBounds(24, 0));
			lbl_noGoals.textAlignmentProperty().set(CompassDirection.CENTER, StatsViewGoals.class);
			panel.add(lbl_noGoals);
		}
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
		private static final Identifier ID_BG      = BSprites.gui_editor_goal_listedGoalBg();
		private static final Identifier ID_BG_DONE = BSprites.gui_editor_goal_listedGoalBgDone();
		// --------------------------------------------------
		private final @NotNull  T                 goal;
		private final @Nullable McbsGoalGUI<T>    goalGui;
		private final @NotNull  McbsFile          file;
		private final @NotNull  StatsView.Filters filters;
		// --------------------------------------------------
		private final @NotNull TTextureElement    el_icon;
		private final @NotNull TLabelElement      el_lblTitle;
		private final @NotNull BGoalProgressBar   el_progrBar;
		private final @NotNull TLabelElement      el_lblProgr;
		private final @NotNull TButtonWidget      el_btnEdit;
		private final @NotNull TButtonWidget      el_btnDelete;
		// ==================================================
		public ListedGoalGui(@NotNull StatsInitContext context, @NotNull T goal)
		{
			//initialize fields
			Objects.requireNonNull(context);
			final var tab = context.getTab();
			this.goal     = Objects.requireNonNull(goal);
			this.goalGui  = McbsGoalGUI.findFor(goal);
			this.file     = tab.getMcbsFile();
			this.filters  = context.getFilters();

			//initialize gui elements
			this.el_icon      = new TTextureElement(BSprites.gui_editor_goal_listedGoalIconBg());
			this.el_lblTitle  = new TLabelElement(this.goal.getObjectiveText());
			this.el_lblTitle.dropShadowProperty().set(false, ListedGoalGui.class);
			this.el_progrBar  = new BGoalProgressBar(this.goal.getProgress(this.file));
			this.el_lblProgr  = new TLabelElement(this.goal.getProgressText(this.file));
			this.el_lblProgr.textScaleProperty().set(0.85d, ListedGoalGui.class);
			this.el_btnEdit   = new TButtonWidget();
			this.el_btnEdit.tooltipProperty().set(
					_ -> TTooltip.of(BLanguage.gui_statsview_stats_mcbsGoals_editBtn()),
					ListedGoalGui.class);
			this.el_btnDelete = new TButtonWidget();
			this.el_btnDelete.tooltipProperty().set(
					_ -> TTooltip.of(BLanguage.gui_statsview_stats_mcbsGoals_deleteBtn()),
					ListedGoalGui.class);

			//initialize button functionality
			this.el_btnEdit.eClicked.addListener(btn -> {
				final @NotNull  var client     = Objects.requireNonNull(btn.getClient(), "Missing 'client' instance");
				final @Nullable var editScreen = Optional.ofNullable(this.goalGui)
						.map(gui -> gui.createEditScreen(this.goal, client.screen))
						.orElse(null);
				if(editScreen != null) {
					client.setScreen(editScreen);
					tab.addEditCount();
				} else {
					btn.enabledProperty().set(false, ListedGoalGui.class);
					btn.tooltipProperty().set(
							_ -> TTooltip.of(BLanguage.gui_statsview_stats_mcbsGoals_noEditGui()),
							ListedGoalGui.class);
				}
			});
			this.el_btnDelete.eClicked.addListener(_ -> tab.removeGoal(this.goal));
		}
		// ==================================================
		public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			if(!this.goal.isDone(this.file))
				pencil.drawGuiSprite(ID_BG, bb.x, bb.y, bb.width, bb.height, 0xFFFFFFFF);
			else pencil.drawGuiSprite(ID_BG_DONE, bb.x, bb.y, bb.width, bb.height, 0xFFFFFFFF);
		}
		// --------------------------------------------------
		protected final @Override void initCallback()
		{
			// ---------- preparation
			final var bb       = getBounds();
			final var pad      = 5;
			final var bb_ico   = new Bounds2i(
					bb.x + pad,             //x
					bb.y + pad,             //y
					bb.height - (pad * 2),  //width
					bb.height - (pad * 2)); //height
			final var bb_right   = new Bounds2i(
					bb_ico.endX + pad + 3,                     //x
					bb.y        + pad + 3,                     //y
					bb.width  - bb_ico.width - (pad * 2) - 12, //width
					bb.height                - (pad * 2) - 6); //height
			final var progress = this.goal.getProgress(this.file);

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
			el_rightBtns.visibleProperty().set(
					this.filters.getProperty(boolean.class, FID_MANAGE_GOALS, false),
					ListedGoalGui.class);
			add(el_rightBtns);

			//goal title label
			this.el_lblTitle.setBounds(0, 0, bb_right.width, 10);
			this.el_lblTitle.setText(this.goal.getObjectiveText());
			el_right.addRel(this.el_lblTitle);

			//goal progress bar
			this.el_progrBar.setBounds(0, 14, (int) (bb_right.width / 2.7d), 14);
			this.el_progrBar.setValue(progress);
			el_right.addRel(this.el_progrBar);

			//goal progress label
			this.el_lblProgr.setBounds(
					this.el_progrBar.getBounds().endX + 3,
					this.el_progrBar.getBounds().y,
					bb_right.width / 3,
					this.el_progrBar.getBounds().height);
			this.el_lblProgr.setText(Component.literal("")
					.append(Component.literal(new DecimalFormat("#%").format(progress)).withColor(0xFFf3e7b7))
					.append("  ")
					.append(this.goal.getProgressText(this.file)));
			el_right.add(this.el_lblProgr);

			// ---------- far-right-side buttons
			if(this.goalGui != null && this.goalGui.isEditable())
				placeRightSideButton(el_rightBtns, this.el_btnEdit, BSprites.gui_icon_pencil());
			placeRightSideButton(el_rightBtns, this.el_btnDelete, BSprites.gui_icon_trashRed());
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
	//                                   SeparatorElement IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * Basic {@link TElement} implementation that draws a horizontal line.
	 */
	@ApiStatus.Internal
	private static final class SeparatorElement extends TElement
	{
		// ==================================================
		public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
			final var bb = getBounds();
			pencil.fillColor(bb.x, bb.y + (bb.height / 2), bb.width, 1, 0x44FFFFFF);
		}
		// ==================================================
		public static final void init(@NotNull TPanelElement panel) {
			final var s = new SeparatorElement();
			s.setBounds(panel.computeNextYBounds(7, GAP));
			panel.add(s);
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
