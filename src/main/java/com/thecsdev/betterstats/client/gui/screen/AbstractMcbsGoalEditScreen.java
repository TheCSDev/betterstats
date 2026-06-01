package com.thecsdev.betterstats.client.gui.screen;

import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorFileTab;
import com.thecsdev.betterstats.api.mcbs.model.goal.McbsGoal;
import com.thecsdev.betterstats.resource.BLanguage;
import com.thecsdev.common.math.UDim2;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.misc.TFillColorElement;
import com.thecsdev.commonmc.api.client.gui.screen.ILastScreenProvider;
import com.thecsdev.commonmc.api.client.gui.screen.TScreen;
import com.thecsdev.commonmc.api.client.gui.screen.TScreenPlus;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Abstract {@link TScreen} implementation that provides a template for
 * constructing {@link McbsGoal} editing interfaces.
 */
public abstract class AbstractMcbsGoalEditScreen<G extends McbsGoal>
		extends TScreenPlus implements ILastScreenProvider
{
	// ==================================================
	private final @Nullable Screen            lastScreen;
	private final @NotNull  McbsEditorFileTab editorTab;
	private final @NotNull  G                 goal;
	// ==================================================
	@SuppressWarnings("unchecked")
	public AbstractMcbsGoalEditScreen(
			@Nullable Screen lastScreen, @NotNull McbsEditorFileTab editorTab, @NotNull G goal)
	{
		super(BLanguage.gui_statsview_stats_mcbsGoals_editBtn()
				.append(": ")
				.append(goal.getObjectiveText()));
		this.lastScreen  = lastScreen;
		this.editorTab   = Objects.requireNonNull(editorTab);
		this.goal        = Objects.requireNonNull(goal);
	}
	// ==================================================
	/**
	 * Returns the {@link McbsEditorFileTab} that the user used to open
	 * this {@link McbsGoal} editing interface.
	 * <p>
	 * <b>Note:</b> Unless there is a good reason for it; Avoid making
	 * any changes to the tab or its underlying data, for any reason.
	 * Intended for reading purposes only.
	 */
	public final @NotNull McbsEditorFileTab getEditorTab() { return editorTab; }

	/**
	 * The {@link McbsGoal} this interface edits.
	 */
	public final @NotNull G getGoal() { return goal; }
	// ==================================================
	public final @Override @Nullable Screen getLastScreen() { return this.lastScreen; }
	// --------------------------------------------------
	protected final @Override void initCallback()
	{
		//the main content element
		final var el_body = new TElement();
		add(el_body);
		el_body.setBounds(new UDim2(0.1, 0, 0, 5), new UDim2(0.8, 0, 1, -10));

		//header
		final var el_header = new TFillColorElement(0xFF444444, 0xFF000000);
		el_body.add(el_header);
		el_header.setBounds(UDim2.ZERO, new UDim2(1, 0, 0, 20));

		final var lbl_title = new TLabelElement(titleProperty().get());
		el_header.add(lbl_title);
		lbl_title.setBounds(el_header.getBounds());
		lbl_title.textAlignmentProperty().set(CompassDirection.CENTER, AbstractMcbsGoalEditScreen.class);

		//footer
		final var el_footer = new TFillColorElement(0xFF444444, 0xFF000000);
		//FIXME - IMPLEMENT THE REST
	}
	// ==================================================
}
