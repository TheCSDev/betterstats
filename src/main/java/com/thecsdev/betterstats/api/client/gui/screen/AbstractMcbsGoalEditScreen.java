package com.thecsdev.betterstats.api.client.gui.screen;

import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorFileTab;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoal;
import com.thecsdev.betterstats.api.mcbs.view.goal.McbsGoalGUI;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils;
import com.thecsdev.betterstats.client.gui.widget.BGoalProgressBar;
import com.thecsdev.betterstats.resource.BLanguage;
import com.thecsdev.common.math.UDim2;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.misc.TFillColorElement;
import com.thecsdev.commonmc.api.client.gui.misc.TSeparatorElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.screen.ILastScreenProvider;
import com.thecsdev.commonmc.api.client.gui.screen.TScreen;
import com.thecsdev.commonmc.api.client.gui.screen.TScreenPlus;
import com.thecsdev.commonmc.api.client.gui.tooltip.TTooltip;
import com.thecsdev.commonmc.api.client.gui.widget.TButtonWidget;
import com.thecsdev.commonmc.resource.TLanguage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import static com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils.GAP;

/**
 * Abstract {@link TScreen} implementation that provides a template for
 * constructing {@link McbsGoal} editing interfaces.
 */
@Environment(EnvType.CLIENT)
public abstract class AbstractMcbsGoalEditScreen<G extends McbsGoal>
		extends TScreenPlus implements ILastScreenProvider
{
	// ================================================== ==================================================
	//                         AbstractMcbsGoalEditScreen IMPLEMENTATION
	// ================================================== ==================================================
	private final @Nullable Screen            lastScreen;
	private final @NotNull  McbsEditorFileTab editorTab;
	// --------------------------------------------------
	private final @NotNull  Identifier        goalId;
	private       @NotNull  G                 goal;
	private       @NotNull  G                 preview;
	private final @Nullable McbsGoalGUI<G>    goalGui;
	// --------------------------------------------------
	private final @NotNull  Interface         el_interface;
	// ==================================================
	/**
	 * Constructs an instance of this screen.
	 * @param lastScreen The {@link Screen} that came before this one.
	 * @param editorTab  The {@link McbsEditorFileTab} where the {@link McbsGoal} is from.
	 * @param goal       The {@link McbsGoal} contained within the {@link McbsEditorFileTab}.
	 * @throws NoSuchElementException If {@link McbsEditorFileTab} does not contain the {@link McbsGoal}.
	 * @throws IllegalStateException  If {@link McbsGoal#clone()} fails to clone the goal.
	 */
	@SuppressWarnings("unchecked")
	public AbstractMcbsGoalEditScreen(
			@Nullable Screen lastScreen, @NotNull McbsEditorFileTab editorTab, @NotNull G goal)
			throws NoSuchElementException, IllegalStateException
	{
		//initialize super
		super(BLanguage.gui_statsview_stats_mcbsGoals_editBtn()
				.append(": ")
				.append(goal.getObjectiveText()));

		//initialize fields
		this.lastScreen   = lastScreen;
		this.editorTab    = Objects.requireNonNull(editorTab);
		this.goalId       = editorTab.getGoals().entrySet().stream()
				.filter(entry -> entry.getValue() == goal)
				.map(Map.Entry::getKey)
				.findFirst()
				.orElseThrow(() -> new NoSuchElementException("Editor tab doesn't contain the given goal"));
		this.goal         = Objects.requireNonNull(goal);
		this.preview      = (G) goal.clone();
		this.goalGui      = McbsGoalGUI.findFor(goal);

		//initialize other
		this.el_interface = new Interface();
	}
	// ==================================================
	/**
	 * Returns the {@link McbsEditorFileTab} that the user used to open
	 * this {@link McbsGoal} editing interface.
	 * <p>
	 * <b>Important note:</b><br>
	 * Unless there is a good reason for it; Avoid making any changes to
	 * the tab or its underlying data, for any reason. Intended for
	 * reading purposes only.
	 */
	protected final @NotNull McbsEditorFileTab getEditorTab() { return editorTab; }

	/**
	 * Returns the {@link McbsGoal}'s {@link Identifier} that is used
	 * when applying changes via {@link #applyChanges()}.
	 */
	protected final @NotNull Identifier getGoalID() { return this.goalId; }

	/**
	 * Returns the original {@link McbsGoal} instance contained by the
	 * {@link #getEditorTab()}, that will eventually be replaced once
	 * {@link #applyChanges()} is called.
	 * <p>
	 * <b>Important note:</b><br>
	 * Not intended to be modified. For reading purposes only. Please
	 * modify {@link #getPreviewGoal()} instead, and then call
	 * {@link #applyChanges()}. This object is solely for accessing
	 * original property values.
	 */
	protected final @NotNull G getGoal() { return this.goal; }

	/**
	 * Returns the {@link McbsGoal} this interface edits. After submission,
	 * the real goal is replaced with this 'preview' one.
	 */
	protected final @NotNull G getPreviewGoal() { return this.preview; }
	// ==================================================
	public final @Override boolean isAllowingInGameHud() { return false; }
	public final @Override @Nullable Screen getLastScreen() { return this.lastScreen; }
	// --------------------------------------------------
	protected final @Override void initCallback() {
		add(this.el_interface);
		this.el_interface.setBounds(new UDim2(0.05, 0, 0, 10), new UDim2(0.9, 0, 1, -20));
	}
	// ==================================================
	/**
	 * Initializes GUI interface for editing the {@link #getPreviewGoal()},
	 * to a given {@link TElement} that acts as the baseline "body".
	 * @param body The {@link TElement} where the editing iterface is to be placed.
	 */
	protected abstract void initGoalEditorGui(@NotNull TElement body);
	// ==================================================
	/**
	 * Applies changes by replacing the {@link McbsEditorFileTab}'s existing
	 * {@link #getGoal()} instance with the {@link #getPreviewGoal()} instance.
	 * Then, a new preview instance is generated here to allow further editing.
	 */
	@SuppressWarnings("unchecked")
	public final void applyChanges()
	{
		//generate a new preview
		final G copy = (G) this.preview.clone(); //copy first; makes exceptions recoverable
		this.goal    = this.preview;
		this.preview = copy;

		//apply changes to the mcbs-file
		this.editorTab.putGoal(this.goalId, this.goal);
	}

	/**
	 * Refreshes the goal preview interface. Call this whenever changes are
	 * made to {@link #getPreviewGoal()}.
	 */
	public final void refreshPreview() {
		if(this.el_interface.getParent() != this)
			return;
		this.el_interface.el_left.clearAndInit();
	}
	// ================================================== ==================================================
	//                                          Interface IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link TElement} implementation holding the main interface for the
	 * {@link AbstractMcbsGoalEditScreen}.
	 */
	@ApiStatus.Internal
	private final class Interface extends TFillColorElement
	{
		// ==================================================
		private final @NotNull  McbsEditorFileTab editorTab = Objects.requireNonNull(AbstractMcbsGoalEditScreen.this.editorTab);
		// --------------------------------------------------
		private       @NotNull  G                 goal      = AbstractMcbsGoalEditScreen.this.goal;
		private       @NotNull  G                 preview   = AbstractMcbsGoalEditScreen.this.preview;
		private final @Nullable McbsGoalGUI<G>    goalGui   = AbstractMcbsGoalEditScreen.this.goalGui;
		// --------------------------------------------------
		final @ApiStatus.Internal TElement el_left;
		final @ApiStatus.Internal TElement el_right;
		// ==================================================
		public Interface() {
			super(0x3B000000, 0xFF000000);
			this.el_left = new TFillColorElement(0x3B000000, 0) {
				protected final @Override void initCallback() { initGoalPreviewGui(el_left); }
			};
			this.el_right = new TElement() {
				protected final @Override void initCallback() { initGoalEditorGui(el_right); }
			};
		}
		// ==================================================
		protected final @Override void initCallback()
		{
			//refresh field values
			this.goal    = AbstractMcbsGoalEditScreen.this.goal;
			this.preview = AbstractMcbsGoalEditScreen.this.preview;

			//add baseline bodies for the gui
			add(this.el_left);
			this.el_left.setBounds(new UDim2(0, 1, 0, 1), new UDim2(0.35, -1, 1, -2));
			add(this.el_right);
			this.el_right.setBounds(new UDim2(0.35, 0, 0, 1), new UDim2(0.65, -1, 1, -2));
		}
		// ==================================================
		/**
		 * Initializes GUI interface for previewing the relevant {@link McbsGoal}s.
		 * @param body The {@link TElement} where the preview interface is to be placed.
		 * @throws NullPointerException If the argument is {@code null}.
		 */
		@ApiStatus.Internal
		private final void initGoalPreviewGui(@NotNull TElement body)
				throws NullPointerException
		{
			//initialize scrollable panel element
			final var el_panel = new TPanelElement.Paintable(0, 0, 0x33FFFFFF);
			el_panel.setBounds(body.getBounds());
			el_panel.scrollPaddingProperty().set(7, Interface.class);
			body.add(el_panel);

			//group label that says "Preview"
			final var lbl_group = StatsViewUtils.initGroupLabel(el_panel, TLanguage.misc_preview());
			lbl_group.textAlignmentProperty().set(CompassDirection.CENTER, Interface.class);

			//preview for "From:"
			final var lbl_from = StatsViewUtils.initGroupLabel(el_panel, TLanguage.misc_from());
			lbl_from.textColorProperty().set(0xFFC6C6C6, Interface.class);
			initGoalPreviewGui(el_panel, this.goal);

			//preview for "To:"
			final var lbl_to = StatsViewUtils.initGroupLabel(el_panel, TLanguage.misc_to());
			lbl_to.textColorProperty().set(0xFFC6C6C6, Interface.class);
			initGoalPreviewGui(el_panel, this.preview);

			//separator for buttons below
			final var el_separator = new TSeparatorElement();
			el_separator.setBounds(el_panel.computeNextYBounds(15, GAP).add(10, 0, -20, 0));
			el_panel.add(el_separator);

			//holder element that holds action buttons
			final var el_btns = new TElement();
			el_btns.setBounds(el_panel.computeNextYBounds(20, GAP));
			el_panel.add(el_btns);

			//submit button
			final var btn_submit = new TButtonWidget();
			btn_submit.setBounds(el_btns.getBounds().endX - 35, el_btns.getBounds().y, 35, 20);
			btn_submit.getLabel().setText(Component.literal("✔"));
			btn_submit.eClicked.addListener(_ -> {
				AbstractMcbsGoalEditScreen.this.applyChanges();
				AbstractMcbsGoalEditScreen.this.close();
			});
			el_btns.add(btn_submit);

			//cancel button
			final var btn_cancel = new TButtonWidget();
			btn_cancel.setBounds(el_btns.getBounds().endX - 73, el_btns.getBounds().y, 35, 20);
			btn_cancel.getLabel().setText(Component.literal("✖"));
			btn_cancel.eClicked.addListener(_ -> AbstractMcbsGoalEditScreen.this.close());
			el_btns.add(btn_cancel);
		}

		/**
		 * Initializes GUI interface for previewing a specific {@link McbsGoal}.
		 * @param panel The {@link TPanelElement} where the GUI interface goes.
		 * @param goal The {@link McbsGoal} to preview.
		 * @throws NullPointerException If an argument is {@code null}.
		 */
		@ApiStatus.Internal
		private final void initGoalPreviewGui(@NotNull TPanelElement panel, @NotNull G goal)
				throws NullPointerException
		{
			//initialize base element for holding icon and objective text
			final var el1 = new TElement();
			el1.setBounds(panel.computeNextYBounds(20, GAP));
			panel.add(el1);

			//initialize goal icon
			final var el_ico = new TFillColorElement(0x3B000000, 0xFF000000);
			el_ico.setBounds(el1.getBounds().x, el1.getBounds().y, 20, 20);
			el1.add(el_ico);

			if(this.goalGui != null)
				this.goalGui.initIcon(goal, el_ico, el_ico.getBounds().height / 10);

			//initialize goal objective label
			final var lbl_objective = new TLabelElement(goal.getObjectiveText());
			lbl_objective.setBounds(el1.getBounds().add(25, 0, -25, -10));
			lbl_objective.tooltipProperty().set(_ -> TTooltip.of(lbl_objective.getText()), Interface.class);
			lbl_objective.hoverableProperty().set(true, Interface.class);
			el1.add(lbl_objective);

			//initialize goal progress label
			final var lbl_progress = new TLabelElement(goal.getProgressText(this.editorTab.getMcbsFile()));
			lbl_progress.setBounds(el1.getBounds().add(25, 10, -25, -10));
			lbl_progress.textScaleProperty().set(0.9, Interface.class);
			lbl_progress.textColorProperty().set(0xFFC6C6C6, Interface.class);
			el1.add(lbl_progress);

			//initialize goal progress bar
			final var el_progress = new BGoalProgressBar();
			el_progress.setBounds(panel.computeNextYBounds(12, GAP));
			el_progress.setValue(goal.getProgress(this.editorTab.getMcbsFile()));
			panel.add(el_progress);
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
