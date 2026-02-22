package com.thecsdev.betterstats.client.gui.panel;

import com.thecsdev.betterstats.api.mcbs.view.tab.McbsEditorHomepageTabGUI;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

/**
 * {@link TPanelElement} whose interface serves as the local-player's personal
 * space, featuring statistics and utilities related to them.
 * <p>
 * Primarily found in {@link McbsEditorHomepageTabGUI}s.
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public final class PersonalHomePanel extends TPanelElement.Paintable
{
	// ==================================================
	public PersonalHomePanel() {
		outlineColorProperty().set(0xFF000000, PersonalHomePanel.class);
	}
	// ==================================================
	protected final @Override void initCallback()
	{
		//FIXME - Implement feature:
		final var lbl = new TLabelElement(Component.literal("Coming soon..."));
		lbl.setBounds(getBounds());
		lbl.textAlignmentProperty().set(CompassDirection.CENTER, PersonalHomePanel.class);
		add(lbl);
	}
	// ==================================================
}
