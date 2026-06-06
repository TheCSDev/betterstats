package com.thecsdev.betterstats.api.client.gui.screen;

import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorFileTab;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoal;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils;
import com.thecsdev.betterstats.resource.BLanguage;
import com.thecsdev.common.math.UDim2;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.misc.TFillColorElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.screen.ILastScreenProvider;
import com.thecsdev.commonmc.api.client.gui.screen.TScreen;
import com.thecsdev.commonmc.api.client.gui.screen.TScreenPlus;
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
		this.lastScreen  = lastScreen;
		this.editorTab   = Objects.requireNonNull(editorTab);
		this.goalId      = editorTab.getGoals().entrySet().stream()
				.filter(entry -> entry.getValue() == goal)
				.map(Map.Entry::getKey)
				.findFirst()
				.orElseThrow(() -> new NoSuchElementException("Editor tab doesn't contain the given goal"));
		this.goal        = Objects.requireNonNull(goal);
		this.preview     = (G) goal.clone();
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
		final var el_interface = new Interface();
		add(el_interface);
		el_interface.setBounds(new UDim2(0.05, 0, 0, 10), new UDim2(0.9, 0, 1, -20));
	}
	// ==================================================
	/**
	 * Initializes GUI interface for previewing the current live state of
	 * {@link #getPreviewGoal()}.
	 * @param body The {@link TElement} where the preview interface is to be placed.
	 */
	@ApiStatus.Internal
	private final void initGoalPreviewGui(@NotNull TElement body)
	{
		//initialize scrollable panel element
		final var el_panel = new TPanelElement.Paintable(0, 0, 0x33FFFFFF);
		el_panel.setBounds(body.getBounds());
		el_panel.scrollPaddingProperty().set(7, AbstractMcbsGoalEditScreen.class);
		body.add(el_panel);

		//FIXME - implement the interface
		final var lbl_group = StatsViewUtils.initGroupLabel(el_panel, Component.literal("Preview"));
		lbl_group.textAlignmentProperty().set(CompassDirection.CENTER, AbstractMcbsGoalEditScreen.class);
	}
	// --------------------------------------------------
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
		public Interface() { super(0x3B000000, 0xFF000000); }
		// ==================================================
		protected final @Override void initCallback()
		{
			//initialize baseline bodies for the gui
			final var el_left = new TFillColorElement(0x3B000000, 0);
			add(el_left);
			el_left.setBounds(new UDim2(0, 1, 0, 1), new UDim2(0.35, -1, 1, -2));

			final var el_right = new TElement();
			add(el_right);
			el_right.setBounds(new UDim2(0.35, 0, 0, 1), new UDim2(0.65, -1, 1, -2));

			//initialize preview and editing interfaces
			AbstractMcbsGoalEditScreen.this.initGoalPreviewGui(el_left);
			AbstractMcbsGoalEditScreen.this.initGoalEditorGui(el_right);
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
