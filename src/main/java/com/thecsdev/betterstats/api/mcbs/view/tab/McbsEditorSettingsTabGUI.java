package com.thecsdev.betterstats.api.mcbs.view.tab;

import com.thecsdev.betterstats.api.client.gui.screen.BetterStatsConfigScreen;
import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorSettingsTab;
import com.thecsdev.common.math.UDim2;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
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
		//initialize the container panel where the settings gui will go
		final var panel = new TPanelElement.Paintable(0, 0, 0x33FFFFFF);
		add(panel);
		//panel.setBounds(new UDim2(0.1, 0, 0, 0), new UDim2(0.8, 0, 1, 0)); -- old bounding box
		panel.setBounds(new UDim2(0, 0, 0, 0), new UDim2(1, 0, 1, 0));

		//initialize the settings gui
		BetterStatsConfigScreen.initConfigGui(panel);
	}
	// ==================================================
}
