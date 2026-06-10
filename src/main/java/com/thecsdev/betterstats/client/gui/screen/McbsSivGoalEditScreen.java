package com.thecsdev.betterstats.client.gui.screen;

import com.thecsdev.betterstats.api.client.gui.screen.AbstractMcbsGoalEditScreen;
import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorFileTab;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoal;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsSivGoal;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils;
import com.thecsdev.betterstats.resource.BLanguage;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.widget.TScrollBarWidget;
import com.thecsdev.commonmc.api.client.gui.widget.text.TSimpleTextFieldWidget;
import com.thecsdev.commonmc.resource.TLanguage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.NoSuchElementException;

import static com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils.GAP;

/**
 * Goal editing screen for {@link McbsSivGoal}.
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class McbsSivGoalEditScreen extends AbstractMcbsGoalEditScreen<McbsSivGoal>
{
	// ==================================================
	/**
	 * Constructs an instance of this screen.
	 * @param lastScreen The {@link Screen} that came before this one.
	 * @param editorTab  The {@link McbsEditorFileTab} where the {@link McbsGoal} is from.
	 * @param goal       The {@link McbsGoal} contained within the {@link McbsEditorFileTab}.
	 * @throws NoSuchElementException If {@link McbsEditorFileTab} does not contain the {@link McbsGoal}.
	 * @throws IllegalStateException  If {@link McbsGoal#clone()} fails to clone the goal.
	 */
	public McbsSivGoalEditScreen(@Nullable Screen lastScreen, @NotNull McbsEditorFileTab editorTab, @NonNull McbsSivGoal goal) throws NoSuchElementException, IllegalStateException {
		super(lastScreen, editorTab, goal);
	}
	// ==================================================
	protected final @Override void initGoalEditorGui(@NotNull TElement body)
	{
		//preparation
		final var goal  = getPreviewGoal();
		final var stats = getEditorTab().getMcbsFile().getStats();

		//initialize scrollable panel element
		final var el_panel = new TPanelElement.Paintable(0, 0, 0x33FFFFFF);
		el_panel.setBounds(body.getBounds().add(0, 0, -8, 0));
		el_panel.scrollPaddingProperty().set(7, McbsSivGoalEditScreen.class);
		body.add(el_panel);

		//initialize scroll-bar for the panel element
		final var el_scroll = new TScrollBarWidget.Flat(el_panel, TScrollBarWidget.ScrollDirection.VERTICAL);
		el_scroll.setBounds(
				el_panel.getBounds().endX,
				el_panel.getBounds().y,
				8,
				el_panel.getBounds().height);
		body.add(el_scroll);

		//stat type input
		StatsViewUtils.initGroupLabel(el_panel, BLanguage.gui_screen_editSivGoal_statType());
		final var in_statType = new TSimpleTextFieldWidget();
		in_statType.setBounds(el_panel.computeNextYBounds(20, GAP));
		in_statType.placeholderProperty().set(Component.literal("minecraft:used"), McbsSivGoalEditScreen.class);
		in_statType.textProperty().set(goal.getStatType().toString(), McbsSivGoalEditScreen.class);
		in_statType.textProperty().addChangeListener((_, _, n) -> {
			try {
				goal.setStatType(Identifier.parse(n));
				refreshPreview();
			} catch (RuntimeException ignored) {}
		});
		el_panel.add(in_statType);

		//stat subject input
		StatsViewUtils.initGroupLabel(el_panel, BLanguage.gui_screen_editSivGoal_statSubject());
		final var in_statSubject = new TSimpleTextFieldWidget();
		in_statSubject.setBounds(el_panel.computeNextYBounds(20, GAP));
		in_statSubject.placeholderProperty().set(Component.literal("minecraft:air"), McbsSivGoalEditScreen.class);
		in_statSubject.textProperty().set(goal.getStatSubject().toString(), McbsSivGoalEditScreen.class);
		in_statSubject.textProperty().addChangeListener((_, _, n) -> {
			try {
				goal.setStatSubject(Identifier.parse(n));
				refreshPreview();
			} catch (RuntimeException ignored) {}
		});
		el_panel.add(in_statSubject);

		//range input
		StatsViewUtils.initGroupLabel(el_panel, BLanguage.gui_screen_editSivGoal_targetValueRange());
		final var el_rangeBody = new TElement();
		el_rangeBody.setBounds(el_panel.computeNextYBounds(20, GAP));
		el_panel.add(el_rangeBody);
		{
			final var bb      = el_rangeBody.getBounds();
			final int valFrom = stats.getIntValue(goal.getStatType(), goal.getStatSubject());
			final int valTo   = valFrom + (getGoal().getTargetValue() - getGoal().getFromValue());

			final var el1 = new TLabelElement(TLanguage.misc_from());
			el1.setBounds(bb.x, bb.y, 40, 20);
			el1.textAlignmentProperty().set(CompassDirection.CENTER, McbsSivGoalEditScreen.class);
			el1.textColorProperty().set(0xFFC6C6C6, McbsSivGoalEditScreen.class);
			el_rangeBody.add(el1);

			final var el2 = new TSimpleTextFieldWidget();
			el2.setBounds(el1.getBounds().endX, bb.y, 50, 20);
			el2.placeholderProperty().set(Component.literal(Integer.toString(valFrom)), McbsSivGoalEditScreen.class);
			el2.textProperty().set(Integer.toString(goal.getFromValue()), McbsSivGoalEditScreen.class);
			el2.textProperty().addChangeListener((_, _, n) -> {
				try {
					goal.setFromValue(Integer.parseInt(n));
					refreshPreview();
				} catch (RuntimeException ignored) {}
			});
			el_rangeBody.add(el2);

			final var el3 = new TLabelElement(Component.literal("➡"));
			el3.setBounds(el2.getBounds().endX, bb.y, 20, 20);
			el3.textScaleProperty().set(1.3, McbsSivGoalEditScreen.class);
			el3.textAlignmentProperty().set(CompassDirection.CENTER, McbsSivGoalEditScreen.class);
			el_rangeBody.add(el3);

			final var el4 = new TLabelElement(TLanguage.misc_to());
			el4.setBounds(el3.getBounds().endX, bb.y, 40, 20);
			el4.textAlignmentProperty().set(CompassDirection.CENTER, McbsSivGoalEditScreen.class);
			el4.textColorProperty().set(0xFFC6C6C6, McbsSivGoalEditScreen.class);
			el_rangeBody.add(el4);

			final var el5 = new TSimpleTextFieldWidget();
			el5.setBounds(el4.getBounds().endX, bb.y, 50, 20);
			el5.placeholderProperty().set(Component.literal(Integer.toString(valTo)), McbsSivGoalEditScreen.class);
			el5.textProperty().set(Integer.toString(goal.getTargetValue()), McbsSivGoalEditScreen.class);
			el5.textProperty().addChangeListener((_, _, n) -> {
				try {
					goal.setTargetValue(Integer.parseInt(n));
					refreshPreview();
				} catch (RuntimeException ignored) {}
			});
			el_rangeBody.add(el5);
		}
		el_rangeBody.setBounds(el_rangeBody.getContentBounds()); //centering the "range" gui
		el_rangeBody.move(                                       //centering the "range" gui
				((el_panel.getBounds().width - (el_panel.scrollPaddingProperty().getI() * 2)) / 2) - (el_rangeBody.getBounds().width / 2),
				0);

	}
	// ==================================================
}
