package com.thecsdev.betterstats.api.mcbs.view.tab;

import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorHomepageTab;
import com.thecsdev.betterstats.client.gui.panel.BSCreditsPanel;
import com.thecsdev.common.math.UDim2;
import com.thecsdev.commonmc.api.client.gui.widget.TScrollBarWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

/**
 * The GUI implementation for {@link McbsEditorHomepageTab}.
 */
@Environment(EnvType.CLIENT)
public final class McbsEditorHomepageTabGUI extends McbsEditorTabGUI<McbsEditorHomepageTab>
{
	// ==================================================
	public McbsEditorHomepageTabGUI(@NotNull McbsEditorHomepageTab editorTab) throws NullPointerException {
		super(editorTab);
	}
	// ==================================================
	protected final @Override void initTabGuiCallback()
	{
		//FIXME - Implement

		//credits panel
		final var credits = new BSCreditsPanel();
		add(credits);
		credits.setBounds(new UDim2(0.7, -10, 0, 10), new UDim2(0.3, -7, 1, -20));

		final var credBB         = credits.getBounds();
		final var scroll_credits = new TScrollBarWidget.Flat(credits);
		scroll_credits.setBounds(credBB.endX - 1, credBB.y, 8, credBB.height);
		add(scroll_credits);
	}
	// ==================================================
}
