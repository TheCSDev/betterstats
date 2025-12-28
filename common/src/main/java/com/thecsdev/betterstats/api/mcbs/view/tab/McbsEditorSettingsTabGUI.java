package com.thecsdev.betterstats.api.mcbs.view.tab;

import com.thecsdev.betterstats.api.client.gui.screen.BetterStatsConfigScreen;
import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorSettingsTab;
import com.thecsdev.common.math.UDim2;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import com.thecsdev.commonmc.api.client.gui.widget.TScrollBarWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

/**
 * {@link McbsEditorTabGUI} implementation for {@link McbsEditorSettingsTab}.
 */
@Environment(EnvType.CLIENT)
public final class McbsEditorSettingsTabGUI extends McbsEditorTabGUI<McbsEditorSettingsTab>
{
	// ==================================================
	public McbsEditorSettingsTabGUI(@NotNull McbsEditorSettingsTab editorTab) throws NullPointerException {
		super(editorTab);
	}
	// ==================================================
	protected final @Override void initTabGuiCallback()
	{
		//initialize the panel where the settings gui will go
		final var panel = new TPanelElement.Paintable(0, 0, 0x33FFFFFF);
		panel.scrollPaddingProperty().set(10, McbsEditorSettingsTabGUI.class);
		add(panel);
		panel.setBounds(new UDim2(0.1, 0, 0, 0), new UDim2(0.8, -8, 1, 0));

		//initialize a scroll-bar for the panel
		final var pbb    = panel.getBounds();
		final var scroll = new TScrollBarWidget.Flat(panel);
		scroll.setBounds(pbb.endX, pbb.y, 8, pbb.height);
		add(scroll);

		//initialize the settings gui
		BetterStatsConfigScreen.initConfigGui(panel);
	}
	// ==================================================
}
