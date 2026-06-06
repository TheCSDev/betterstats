package com.thecsdev.betterstats.client.gui.screen;

import com.thecsdev.betterstats.api.client.gui.screen.AbstractMcbsGoalEditScreen;
import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorFileTab;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoal;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsSivGoal;
import com.thecsdev.betterstats.api.mcbs.view.statsview.StatsViewUtils;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.widget.TScrollBarWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.NoSuchElementException;

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

		//FIXME - initialize rest of gui
		StatsViewUtils.initGroupLabel(el_panel, Component.literal("Coming soon..."));
	}
	// ==================================================
}
