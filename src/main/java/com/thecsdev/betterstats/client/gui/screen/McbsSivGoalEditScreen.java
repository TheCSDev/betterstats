package com.thecsdev.betterstats.client.gui.screen;

import com.thecsdev.betterstats.api.mcbs.model.goal.McbsSivGoal;
import com.thecsdev.betterstats.resource.BLanguage;
import com.thecsdev.common.math.UDim2;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.misc.TSeparatorElement;
import com.thecsdev.commonmc.api.client.gui.panel.window.TWindowElement;
import com.thecsdev.commonmc.api.client.gui.render.TGuiGraphics;
import com.thecsdev.commonmc.api.client.gui.screen.ILastScreenProvider;
import com.thecsdev.commonmc.api.client.gui.screen.TScreenPlus;
import com.thecsdev.commonmc.api.client.gui.widget.TButtonWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.thecsdev.commonmc.resource.TComponent.gui;

/**
 * Goal editing screen for {@link McbsSivGoal}.
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class McbsSivGoalEditScreen extends TScreenPlus implements ILastScreenProvider
{
	// ================================================== ==================================================
	//                              McbsSivGoalEditScreen IMPLEMENTATION
	// ================================================== ==================================================
	private final @Nullable Screen      lastScreen;
	private final @NotNull  McbsSivGoal goal;
	private final @NotNull  McbsSivGoal mockGoal; //used for goal preview mechanics
	// ==================================================
	public McbsSivGoalEditScreen(
			@Nullable Screen lastScreen, @NotNull McbsSivGoal goal)
			throws NullPointerException
	{
		//initialize main fields
		this.lastScreen = lastScreen;
		this.goal       = Objects.requireNonNull(goal);
		titleProperty().set(
				gui("container/cartography_table/map")
						.append(" ")
						.append(BLanguage.gui_statsview_stats_mcbsGoals_editBtn())
						.append(": ")
						.append(goal.getType().getName()),
				McbsSivGoalEditScreen.class);

		//initialize fields used for goal editing previews
		this.mockGoal = new McbsSivGoal(this.goal);
	}
	// ==================================================
	public final @Override @Nullable Screen getLastScreen() { return this.lastScreen; }
	// --------------------------------------------------
	@SuppressWarnings("UnstableApiUsage")
	public final @Override void renderCallback(@NotNull TGuiGraphics pencil) {
		if(this.lastScreen == null) return;
		this.lastScreen.extractRenderState(pencil.getNative(), pencil.getMouseX(), pencil.getMouseY(), pencil.getDeltaTicks());
		final var bb = this.getBounds();
		pencil.fillColor(bb.x, bb.y, bb.width, bb.height, 587202559);
	}
	// --------------------------------------------------
	protected final @Override void initCallback() {
		final var bb     = getBounds();
		final var dialog = new EditDialog();
		dialog.titleProperty().set(titleProperty().get(), McbsSivGoalEditScreen.class);
		dialog.setBounds((bb.width / 2) - (274 / 2), (bb.height / 2) - (214 / 2), 274, 214);
		add(dialog);
	}
	// ================================================== ==================================================
	//                                         EditDialog IMPLEMENTATION
	// ================================================== ==================================================
	/**
	 * {@link TWindowElement} implementation that acts as a "dialog" that allows
	 * the user to edit an {@link McbsSivGoal}.
	 */
	@ApiStatus.Internal
	private final class EditDialog extends TWindowElement
	{
		// ==================================================
		private final @NotNull McbsSivGoal goal     = McbsSivGoalEditScreen.this.goal;
		private final @NotNull McbsSivGoal mockGoal = McbsSivGoalEditScreen.this.mockGoal;
		// --------------------------------------------------
		private final TLabelElement lbl_preview = new TLabelElement();
		private final TButtonWidget btn_cancel  = new TButtonWidget();
		private final TButtonWidget btn_submit  = new TButtonWidget();
		// ==================================================
		public EditDialog()
		{
			//initialize property values
			backgroundColorProperty().set(-13948117, EditDialog.class);
			closeOperationProperty().set(CloseOperation.CLOSE_SCREEN, EditDialog.class);

			//initialize element properties and functionality
			refreshPreview();
			this.lbl_preview.textColorProperty().set(0xAAFFFFFF, EditDialog.class);
			this.lbl_preview.textScaleProperty().set(0.85, EditDialog.class);
			this.btn_cancel.getLabel().setText(Component.translatable("gui.cancel"));
			this.btn_cancel.eClicked.addListener(_ -> close());
			this.btn_submit.getLabel().setText(Component.translatable("gui.done"));
			this.btn_submit.eClicked.addListener(_ -> {
				this.goal.copyFrom(this.mockGoal);
				close();
			});
		}
		// ==================================================
		protected final @Override void initBodyCallback(@NotNull TElement body)
		{
			//padding element
			final var el_pad = new TElement();
			el_pad.setBounds(body.getBounds().add(5, 5, -10, -10));
			body.add(el_pad);

			//main editing content
			final var el_editor = new TElement();
			el_pad.add(el_editor);
			el_editor.setBounds(UDim2.ZERO, new UDim2(1, 0, 1, -46));
			initBody2(el_editor);

			//separator
			final var el_separator = new TSeparatorElement();
			el_pad.add(el_separator);
			el_separator.setBounds(new UDim2(0, 0, 1, -41), new UDim2(1, 0, 0, 1));

			//action bar
			final var el_actionBar = new TElement();
			el_pad.add(el_actionBar);
			el_actionBar.setBounds(new UDim2(0, 0, 1, -35), new UDim2(1, 0, 0, 35));
			initActionBar(el_actionBar);
		}
		// ==================================================
		/**
		 * Initializes GUI the main goal editing content.
		 * @param body The {@link TElement} to initialize the GUI onto.
		 */
		private final void initBody2(@NotNull TElement body) {}
		// --------------------------------------------------
		/**
		 * Initializes GUI for the preview label, cancel button, and done button.
		 * @param body The {@link TElement} to initialize the GUI onto.
		 */
		private final void initActionBar(@NotNull TElement body)
		{
			//preparation
			final var bb = body.getBounds();

			//preview label
			this.lbl_preview.setBounds(bb.x, bb.y, bb.width, 10);
			this.lbl_preview.textAlignmentProperty().set(CompassDirection.CENTER, EditDialog.class);
			body.add(this.lbl_preview);

			//cancel button
			body.add(this.btn_cancel);
			this.btn_cancel.setBounds(new UDim2(0.5, -102, 1, -20), new UDim2(0, 100, 0, 20));

			//submit button
			body.add(this.btn_submit);
			this.btn_submit.setBounds(new UDim2(0.5, 2, 1, -20), new UDim2(0, 100, 0, 20));
		}
		// ==================================================
		/**
		 * Refreshes the text of {@link #lbl_preview}.
		 */
		private final void refreshPreview() {
			this.lbl_preview.setText(Component
					.literal("\"")
					.append(this.goal.getObjectiveText())
					.append("\""));
		}
		// ==================================================
	}
	// ================================================== ==================================================
}
