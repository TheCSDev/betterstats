package com.thecsdev.betterstats.api.mcbs.view.tab;

import com.thecsdev.betterstats.api.mcbs.controller.tab.McbsEditorHomepageTab;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * The GUI implementation for {@link McbsEditorHomepageTab}.
 */
@ApiStatus.Experimental
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
	}
	// ==================================================
}
