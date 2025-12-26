package com.thecsdev.betterstats.api.mcbs.view.tab;

import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorNullTab;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A {@link McbsEditorTabGUI} implementation that does not display anything.
 */
@Environment(EnvType.CLIENT)
public class McbsEditorNullTabGUI extends McbsEditorTabGUI<McbsEditorNullTab>
{
	// ==================================================
	/**
	 * Main singleton instance of this class.
	 */
	public static final McbsEditorNullTabGUI INSTANCE = new McbsEditorNullTabGUI();
	// ==================================================
	private McbsEditorNullTabGUI() { super(McbsEditorNullTab.INSTANCE); }
	// ==================================================
	protected final @Override void initTabGuiCallback() {}
	// ==================================================
}
