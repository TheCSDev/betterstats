package com.thecsdev.betterstats.api.client.gui.statsview;

import com.thecsdev.betterstats.api.client.gui.McbsEditorGUI;
import com.thecsdev.betterstats.resources.BSSLang;
import com.thecsdev.common.util.enumerations.CompassDirection;
import com.thecsdev.commonmc.api.client.gui.TElement;
import com.thecsdev.commonmc.api.client.gui.label.TLabelElement;
import com.thecsdev.commonmc.api.client.gui.panel.TPanelElement;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Utilities for initializing {@link StatsView} GUI interfaces.
 */
public final class StatsViewUtils
{
	// ==================================================
	/**
	 * The default horizontal and vertical margin that is commonly used for
	 * various GUI elements on {@link McbsEditorGUI}s.
	 */
	public static final int GAP = 3;
	// --------------------------------------------------
	// ==================================================
	private StatsViewUtils() {}
	// ==================================================
	/**
	 * Creates and adds a {@link TLabelElement} to the {@link TPanelElement},
	 * positioned at the bottom of the panel's {@link TElement#getContentBounds()},
	 * and taking up full panel width.
	 * @param panel The target {@link TPanelElement}.
	 * @param text The label's text.
	 * @throws NullPointerException If an argument is {@code null}.
	 */
	@Contract("_, _ -> new")
	public static final @NotNull TLabelElement initGroupLabel(
			@NotNull TPanelElement panel, @NotNull Component text)
			throws NullPointerException
	{
		//require not null for arguments
		Objects.requireNonNull(panel);
		Objects.requireNonNull(text);

		//create and add a new label
		final var label = new TLabelElement();
		label.setBounds(panel.computeNextYBounds(17, GAP));
		label.textProperty().set(text, StatsViewUtils.class);
		label.textColorProperty().set(0xFFFFFF00, StatsViewUtils.class);
		panel.add(label);
		return label;
	}
	// ==================================================
	/**
	 * Initializes the default user-interface for a {@link StatsView}'s "Filters" panel.
	 * @param context The {@link StatsView.FiltersInitContext}.
	 * @throws NullPointerException If the argument is {@code null}.
	 */
	public static void initDefaultFilters(@NotNull StatsView.FiltersInitContext context)
	{
		//obtain the panel
		final var panel = context.getPanel();
		//create the "Filters" group label
		final var lbl_group = initGroupLabel(panel, BSSLang.gui_statstab_filters());
		lbl_group.setBounds(lbl_group.getBounds().add(0, 0, 0, 8));
		lbl_group.textColorProperty().set(-1, StatsViewUtils.class);
		lbl_group.textAlignmentProperty().set(CompassDirection.CENTER, StatsViewUtils.class);
		//FIXME - Implement more GUI stuff here
	}
	// ==================================================
}
